# OCCAM - Digital Computational Archive and Curation Service
# Copyright (C) 2017-2017 wilkie
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
require 'rss'

class Occam
  # Deletes the given comment if permissions are correct and the comment exists.
  delete %r{#{OBJECT_ROUTE_REGEX}/social/comments/(?<comment_id>.*?)$} do
    # Fetch the original comment in order to check that we are allowed to edit
    # it.
    match = @object.viewComments(:simple => true, :only_matching => true, :id => params[:comment_id])
    if match.nil? or match.empty? or current_account.nil? or match[0][:person][:id] != current_person.uuid
      status 404
      return
    end

    record = @object.deleteComment(:comment_id => params[:comment_id])

    record[:numReplies] ||= 0
    record[:replies]    ||= []

    begin
      record[:person].delete(:revision)
      record[:person][:ownerInfo] = record[:person]
      record[:person][:info] = record[:person]
      record[:person][:account] = current_account
      record[:person] = Occam::Person.new(record[:person])
    rescue
      record.delete :person
    end

    if request.xhr?
      content_type "text/html"
      render :haml, :"objects/social/_comment", :layout => false, :locals => {
        :comment => record,
        :object  => @object
      }
    else
      redirect @object.url(:path => "social")
    end
  end

  # Posts the comment to the object.
  post %r{#{OBJECT_ROUTE_REGEX}/social/comments/?$} do
    if current_account.nil?
      status 404
      return
    end

    record = @object.addComment(:comment => params[:comment], :anon => params[:anon], :inReplyTo => params["inReplyTo"])

    record[:numReplies] ||= 0
    record[:replies] ||= []

    begin
      record[:person].delete(:revision)
      record[:person][:ownerInfo] = record[:person]
      record[:person][:info] = record[:person]
      record[:person][:account] = current_account
      record[:person] = Occam::Person.new(record[:person])
    rescue
      record.delete :person
    end

    if request.xhr?
      content_type "text/html"
      render :haml, :"objects/social/_comment", :layout => false, :locals => {
        :comment => record,
        :object  => @object
      }
    else
      redirect @object.url(:path => "social", :query => {:comment => record[:id], :inReplyTo => params["inReplyTo"]})
    end
  end

  # Edit the given comment with the provided changes.
  post %r{#{OBJECT_ROUTE_REGEX}/social/comments/(?<comment_id>.*)$} do
    # Fetch the original comment in order to check that we are allowed to edit
    # it.
    match = @object.viewComments(:simple => true, :only_matching => true, :id => params[:comment_id])
    if not match or current_account.nil? or match[0][:person][:id] != current_person.uuid
      status 404
      return
    end

    record = @object.editComment(:comment => params[:comment], :edit => params[:comment_id])

    record[:numReplies] ||= 0
    record[:replies] ||= []

    begin
      record[:person].delete(:revision)
      record[:person][:ownerInfo] = record[:person]
      record[:person][:info] = record[:person]
      record[:person][:account] = current_account
      record[:person] = Occam::Person.new(record[:person])
    rescue
      record.delete :person
    end

    if request.xhr?
      content_type "text/html"
      render :haml, :"objects/social/_comment", :layout => false, :locals => {
        :comment => record,
        :object  => @object
      }
    else
      redirect @object.url(:path => "social", :query => {:comment => record[:id], :inReplyTo => params["inReplyTo"]})
    end
  end

  # Helper function that fills up the maker feed with comments.
  def appendCommentsToRSS(maker, comments)
    max_time = nil
    comments.each do |comment|
      comment_time = Time.parse(comment[:createTime])
      maker.items.new_item do |item|
        item.guid.content = @object.url(:path => "social/comments/?comment=#{comment[:id]}")
        item.link = @object.url(:path => "social/comments/?comment=#{comment[:id]}")
        item.title = "New comment on \"#{@object.info[:name]}\""
        item.content.content = comment[:content]
        item.updated = comment_time.to_s
      end
      child_max_time = appendCommentsToRSS(maker, comment[:replies])
      # Child must have been created after the parent.
      if child_max_time
        if (max_time and child_max_time > max_time) or not max_time
          max_time = child_max_time
        end
      elsif (max_time and comment_time > max_time) or not max_time
        max_time = comment_time
      end
    end
    return max_time
  end

  # RSS feed for the object.
  get %r{#{OBJECT_ROUTE_REGEX}/feed?$} do
    # TODO: Provide other updates about the object.
    since = request.env['If-Modified-Since']
    if since
      since = Time.parse(since).strftime("%F %T")
    end

    comments = @object.viewComments(:inReplyTo => params["inReplyTo"], :simple => true, :all => true, :since => since)
    content_type "application/atom+xml"
    last_modified = nil

    # Create an ATOM feed.
    rss = RSS::Maker.make("atom") do |maker|
      maker.channel.author = "OCCAM"
      maker.channel.title = @object.info[:name]
      maker.channel.id = @object.url()

      # If nothing was added recently, just report the current time.
      last_modified = appendCommentsToRSS(maker, comments)
      last_modified ||= Time.now

      maker.channel.updated = last_modified.to_s
    end

    headers['Last-Modified'] = last_modified.strftime("%a, %d %b %Y %T GMT")
    rss.to_s
  end

  # Gathers the comments of the object, in a generic sense.
  get %r{#{OBJECT_ROUTE_REGEX}/social/comments/?$} do

    # TODO: should be able to pull HTML or JSON for this
    #       the HTML should render the replies as a list

    @object.viewComments(:inReplyTo => params["inReplyTo"], :simple => true).to_json
  end

  #1980
  post %r{#{OBJECT_ROUTE_REGEX}/metadata/?$} do
    keys_arr = [:name, :collaborators, :website, :source, :backed,
                :generator, :configures, :views, :configuration,
                :environment, :architecture]

    keys_arr.each do |x|
      @object = @object.set([[x, params[x]]]) if params[x] && params[x] != ""
    end

    @object = @object.set([[:generator, nil]])

    redirect @object.url
  end

  # Retrives replies for a comment
  get %r{#{OBJECT_ROUTE_REGEX}/social/comments/(?<comment_id>.*?)/replies/?$} do
    if current_account.nil?
      status 404
      return
    end

    # TODO: ActivityStreams

    format = request.preferred_type(['text/html', 'application/json', 'application/atom+xml', 'application/xml'])
    case format
    when 'application/json'
      content_type "application/json"

      comments = @object.viewComments(:inReplyTo => params[:comment_id], :simple => true)
      comments.to_json
    else
      comments = @object.viewComments(:inReplyTo => params[:comment_id])
      render :haml, :"objects/social/_replies", :layout => !request.xhr?, :locals => {:comments => comments, :object => @object}
    end
  end

  # Retrives a single comment.
  get %r{#{OBJECT_ROUTE_REGEX}/social/comments/(?<comment_id>.*?)/?$} do
    if current_account.nil?
      status 404
      return
    end

    @object.viewComments(:id => params[:comment_id], :simple => true).to_json
  end

  # This routes pushes a run job
  post %r{#{OBJECT_ROUTE_REGEX}/run/?$} do
  end

  # This routes pushes a build job
  post %r{#{OBJECT_ROUTE_REGEX}/build/?$} do
    # Pull out the object
    @object.build()
  end

  # This route creates or links an object into the given object "contains"
  post %r{#{OBJECT_ROUTE_REGEX}/objects/?$} do
    new_object = @object.create(params)

    redirect new_object.url
  end

  # This route creates or links an object into the given workflow
  post %r{#{OBJECT_ROUTE_REGEX}/workflow/?$} do
    params[:type]="configuration"
    params[:name]="temporary_name"
    #new_object = object.create(params)

    #redirect new_object.url
  end

  # Renders the input for an object
  get %r{#{OBJECT_ROUTE_REGEX}/inputs/?((?<input_index>\d+)/?)?$} do
    # Allow AJAX requests for this route
    handleCORS()

    # Determine the input index to render
    index = params[:input_index].to_i

    # Get the node index, if there is one
    nodeIndex = (params["node_index"] || 0).to_i

    inputs = @object.info[:inputs] || []

    if 0 > index || inputs.length <= index
      status 404
      return
    end

    input = inputs[index]

    if input[:type] == "configuration" and !input[:schema].nil?
      # This is a configuration
      # We can render a form for this

      # First, retrieve the schema
      schema = @object.retrieveJSON(input[:schema])

      # Use this to render the form
      return render :haml, :"objects/_configuration", :layout => !request.xhr?, :locals => {
        :reviewing           => @params[:params] && @params[:params].has_key?("review"),
        :object              => @object,
        :revision            => @object.revision,
        :schema              => schema,
        :configuration_index => index,
        :index               => nodeIndex,
        :defaults            => {}
      }
    end

    # This is a normal input
    # Render something else then
    ""
  end

  # Updates a configuration of a workflow object
  post %r{#{OBJECT_ROUTE_REGEX}/configure/(?<node_index>\d+)/(?<wire_index>\d+)/?$} do
    # Get the workflow
    workflow = resolveObject(Occam::Workflow)

    # Get the wire index so we know which configuration this is
    # If there is an existing configuration (And this workflow owns it)
    # We will update it
    nodeIndex = params[:node_index].to_i
    wireIndex = params[:wire_index].to_i

    inputs = workflow.inputsAt(nodeIndex, wireIndex)

    # If there is no object connected, update the workflow to include an empty configuration
    if inputs.empty?
      # Add a blank configuration
      input = workflow.createConfigurationFor(nodeIndex, wireIndex)
      input.info(true)
      workflow = input.parent.as(Occam::Workflow)
      workflow.info(true)

      # Update workflow data to add this to the structure (as a hidden node)
      inputIndex = workflow.append(input)
      workflow.connect(inputIndex, -1, nodeIndex, wireIndex, :visibility => :hidden)
      inputs = [workflow.connectionAt(inputIndex)]
    else
      input = workflow.resolveNode(inputs[0])
    end

    input = input.as(Occam::Configuration)
    input.info(true)

    # Decode the configuration form data sent from the browser
    # data = configuration.decodeData()
    data = Occam::Workflow.decode_base64(params[:data])

    # Submit the new configuration data to the configuration object
    input = input.configure(data)
    input.info(true)

    # Update configuration node in workflow
    inputs[0][:revision] = input.revision

    # Return to the new version of the object
    workflow = workflow.save
    workflow.info(true)
    workflow.data.to_json

    if request.xhr?
    else
      object = @object
      redirect Occam::Object.new(:withinUuid => object.withinUuid, :withinRevision => Occam::Object.new(:uuid => object.withinUuid, :account => current_account).revision, :index => object.index, :root => object.root, :account => current_account, :link => object.link).url
    end
  end

  # Removes an author
  delete %r{#{OBJECT_ROUTE_REGEX}/authorships/(?<person_uuid>[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/?$} do
    role     = params["role"]
    objectId = params[:person_uuid]
    username = params["username"]

    record = Occam::Authorship.new(:role    => role,
                                   :object  => @object,
                                   :name    => username,
                                   :person  => Occam::Person.new(:uuid => objectId),
                                   :account => current_account)

    record.destroy!

    if request.referrer
      redirect request.referrer
    else
      redirect @object.url
    end
  end

  # Adds an author
  post %r{#{OBJECT_ROUTE_REGEX}/authorships/?$} do
    role     = params["role"]
    objectId = params["object-id"]
    username = params["username"]

    Occam::Authorship.create(:role    => role,
                             :object  => @object,
                             :name    => username,
                             :person  => Occam::Person.new(:uuid => objectId),
                             :account => current_account)

    if request.referrer
      redirect request.referrer
    else
      redirect @object.url
    end
  end

  # Creates a review-link
  post %r{#{OBJECT_ROUTE_REGEX}/review-links/?$} do
    Occam::ReviewCapability.create(@object)

    if request.xhr?
    else
      if request.referrer
        redirect request.referrer
      else
        redirect @object.url
      end
    end
  end

  # Removes a review-link
  delete %r{#{OBJECT_ROUTE_REGEX}/review-links/?$} do
    reviewLink = Occam::ReviewCapability.new(:object  => @object,
                                             :account => current_account)
    reviewLink.destroy!

    if request.referrer
      redirect request.referrer
    else
      redirect @object.url
    end
  end

  # Updates object permissions
  post %r{#{OBJECT_ROUTE_REGEX}/permissions/?$} do
    key     = params["update"]
    value   = params["value"]
    person  = params["person"]
    person  = params["object-id"] || person
    section = params["section"]

    if person
      # Pull out the object
      person = Occam::Person.new(:uuid => person)
    end

    if value == ""
      value = nil
    else
      value = value == "on"
    end

    # Update permission
    row = @object.setPermission(key, value, section == "children", person)

    if request.xhr?
      render :haml, :"objects/permissions/_row",
                    :layout => false,
                    :locals => {
        :access  => row,
        :section => section,
        :object  => @object,
      }
    else
      if request.referrer
        redirect request.referrer
      else
        redirect @object.url(:path => "access")
      end
    end
  end

  # Deletes the permissions for a particular person.
  delete %r{#{OBJECT_ROUTE_REGEX}/permissions/?$} do
    person  = params["person"]
    section = params["section"]

    if person
      # Pull out the object
      person = Occam::Person.new(:uuid => person)
    end

    # Delete permission row
    @object.resetPermission(section == "children", person)

    if request.xhr?
    else
      if request.referrer
        redirect request.referrer
      else
        redirect @object.url(:path => "access")
      end
    end
  end

  # Fork an object to the given object
  post %r{#{OBJECT_ROUTE_REGEX}/fork/?$} do
    # Determine index
    params["to"]["index"] = params["to"]["index"].split("/").map(&:to_i)

    # Symbolize the keys so we can pass them to Occam::Object.new
    params["to"] = params["to"].symbolize_keys

    # Get the object we are cloning 'to'
    to = Occam::Object.new(params["to"])

    # Clone
    newObject = @object.clone(to, :name => params["name"])

    # Return the new object information if that is requested, otherwise redirect to the new object
    format = request.preferred_type(['text/html', 'application/json', 'application/atom+xml', 'application/xml'])
    case format
    when 'application/json'
      content_type "application/json"

      # When json is requested, this is considered an API call and we will return
      # the command's output
      {
        :object => {
          :id => newObject.withinUuid,
          :revision => newObject.withinRevision,
          :index => newObject.index,
          :link => newObject.link
        },
        :url => newObject.url
      }.to_json
    else
      # When html is requested (common case)
      # We will redirect to the html content (the object itself)
      redirect newObject.url
    end
  end

  def render_qrcode(format = nil)
    if format.nil?
      format = request.preferred_type(['image/png', 'image/svg+xml', 'application/svg'])
    end

    case format
    when 'application/svg', 'image/svg+xml'
      type = :svg
    else
      type = :png
    end

    if request.xhr?
      content_type "text/html"
      render :haml, :"objects/qrcode", :layout => false, :locals => {
        :object => @object
      }
    elsif type == :svg
      content_type "image/svg+xml"
      @object.qrcode(request, :type => :svg)
    else
      content_type "image/png"
      @object.qrcode(request, :type => :png)
    end
  end

  # Retrieve form to fork the object
  get %r{#{OBJECT_ROUTE_REGEX}/fork/?$} do
    render :haml, :"objects/fork", :layout => !request.xhr?, :locals => {
      :object => @object,
      :errors => []
    }
  end

  # Retrieve the qrcode
  get %r{#{OBJECT_ROUTE_REGEX}/qrcode$} do
    render_qrcode
  end

  # Retrieve the qrcode (as a png)
  get %r{#{OBJECT_ROUTE_REGEX}/qrcode.png$} do
    render_qrcode("image/png")
  end

  # Retrieve the qrcode (as an svg)
  get %r{#{OBJECT_ROUTE_REGEX}/qrcode.svg$} do
    render_qrcode("application/svg")
  end

  # Retrieve queued workflow runs
  get %r{#{OBJECT_ROUTE_REGEX}/runs/?$} do
    # Pull out the object (as a workflow)
    workflow = resolveObject(Occam::Workflow)

    content_type "application/json"
    workflow.runs.to_json
  end

  # Queue a build
  post %r{#{OBJECT_ROUTE_REGEX}/builds/?$} do
    # Pull out the object (as a workflow, if needed)
    run = @object.build

    format = request.preferred_type(['text/html', 'application/json', 'application/atom+xml', 'application/xml'])

    case format
    when 'application/json'
      content_type "application/json"
      run.to_json
    else
      render :haml, :"objects/_run-list-item", :layout => false, :locals => {
        :run => run,
        :object => @object
      }
    end
  end

  # Queue a run
  post %r{#{OBJECT_ROUTE_REGEX}/runs/?$} do
    input = nil
    if params[:input_object_id]
      input = Occam::Object.new(:id       => params[:input_object_id],
                                :revision => params[:input_object_revision],
                                :account  => @object.account)
    end

    # Pull out the object (as a workflow, if needed)
    if @object.info[:type] == "workflow" || @object.info[:type] == "experiment"
      workflow = resolveObject(Occam::Workflow)
      runInfo = workflow.queue
      run = Occam::Run.new(:id => runInfo[:id], :info => runInfo)
    else
      interactive = params[:interactive] == true || params[:interactive] == "true"
      run = @object.run(:input => input, :interactive => interactive)
    end

    format = request.preferred_type(['text/html', 'application/json', 'application/atom+xml', 'application/xml'])

    case format
    when 'application/json'
      content_type "application/json"
      run.to_json
    else
      render :haml, :"objects/_run-list-item", :layout => false, :locals => {
        :run => run,
        :object => @object
      }
    end
  end

  # Cancel the job
  delete %r{#{OBJECT_ROUTE_REGEX}/jobs/?((?<job_index>\d+)/?)?$} do
    job = Occam::Job.new(:id      => params[:job_index],
                         :account => @object.account)

    job.cancel

    if request.xhr?
      content_type "application/json"
      job.info.to_json
    elsif request.referrer
      redirect request.referrer
    end
  end

  # Retrieve the job task instantiation
  get %r{#{OBJECT_ROUTE_REGEX}/jobs/?((?<job_index>\d+)/task/?)?$} do
    job = Occam::Job.new(:id      => params[:job_index],
                         :account => @object.account)

    info = job.taskInfo
    if not info
      status 404
      return
    end

    content_type "application/json"
    info.to_json
  end

  # Retrieve the job network instantiation
  get %r{#{OBJECT_ROUTE_REGEX}/jobs/?((?<job_index>\d+)/network/?)?$} do
    job = Occam::Job.new(:id      => params[:job_index],
                         :account => @object.account)

    info = job.networkInfo
    if not info
      status 404
      return
    end

    content_type "application/json"
    info.to_json
  end

  # Retrieve the job status
  get %r{#{OBJECT_ROUTE_REGEX}/jobs/?((?<job_index>\d+)/?)?$} do
    job = Occam::Job.new(:id      => params[:job_index],
                         :account => @object.account)

    content_type "application/json"
    {"job" => job.info}.to_json
  end

  # Retrieve the workflow run status
  get %r{#{OBJECT_ROUTE_REGEX}/runs/?((?<run_index>\d+)/?)?$} do
    # Pull out the object (as a workflow)
    workflow = resolveObject(Occam::Workflow)

    run = workflow.runInfo(:runID => params[:run_index])

    run.to_json
  end

  # Updates a file within an object
  post %r{#{OBJECT_ROUTE_REGEX}/files/(?<path>.*)$} do

    # if request.xhr?
      # { }.to_json
    # else
      # redirect
    # end



    # If we have a file... upload that file
    # 1980
    puts "HEY YOU"
    puts params[:fileToUpload][:filename]
    if params[:fileToUpload]
      if params[:fileToUpload][:tempfile]
        @object = @object.set(params[:fileToUpload][:tempfile].read, params[:path] + "/" + params[:fileToUpload][:filename])
      end


    redirect @object.url(:path => "files/" + params[:path] + params[:fileToUpload][:filename])

    # do only if file is being edited
    # Parse the items given (TODO: Why is it like this????? Why doesn't the client do this??)
    else
      items=[]
      JSON.parse(params[:data]).each do |key, value|
        items << [key, value.to_json]
      end

      # Update the object
      @object = @object.set(items, params[:path], :type => "json")

    end

    format = request.preferred_type(['application/json'])
    case format
    when 'application/json'
      content_type "application/json"

      # When json is requested, respond with the API return data
      {
        :url => request.referrer
      }.to_json
    else
      # In the common case, redirect to the updated file
      redirect @object.url(:path => "files/" + params[:path])
    end
  end


  # Gets the file content of the object
  get %r{#{OBJECT_ROUTE_REGEX}/file/?$} do
    format = request.preferred_type(['text/html', 'text/html+configuration', 'application/json'])
    case format
    when "text/html+configuration"
      # Render the object data as a configuration form

      # First, retrieve the schema
      schema = @object.schema

      # Use this to render the form
      return render :haml, :"objects/_configuration", :layout => !request.xhr?, :locals => {
        :reviewing           => @params[:params] && @params[:params].has_key?("review"),
        :object              => @object,
        :revision            => @object.revision,
        :schema              => schema,
        :configuration_index => 0,
        :index               => 0,
        :defaults            => {}
      }
    else
      filePath = @object.file
      redirect @object.url(:path => "files/#{filePath}")
    end
  end

  # Gets the contents of the object
  get %r{#{OBJECT_ROUTE_REGEX}/contains/?$} do
    format = request.preferred_type(['text/html', 'application/json'])
    case format
    when 'application/json'
      content_type "application/json"

      @object.contents.map(&:info).to_json
    else
      render :haml, :"objects/_object_tree", :layout => !request.xhr?, :locals => {
        :root    => false,
        :type    => params["type"],
        :objects => @object.contents
      }
    end
  end

  options %r{#{OBJECT_ROUTE_REGEX}/raw/(?<path>.+)?$} do
    # Resolve the object a second time to set the path
    @object = resolveObject()

    handleCORS('*')

    status 200
  end

  get %r{#{OBJECT_ROUTE_REGEX}/citations/(?<format>.+)$} do
    @object.citation(:format => params[:format])
  end

  # Renders an object view
  #
  # Examples:
  # GET /bb79a35e-528e-11e6-bb9d-f23c910a26c8/fbbe5cadd1c5afc73348fb2fa6b51c624953082e/run
  #   Look at this object's run tab
  # GET /bb79a35e-528e-11e6-bb9d-f23c910a26c8/fbbe5cadd1c5afc73348fb2fa6b51c624953082e/raw/object.json
  #   Download this object's object.json metadata
  # GET /bb79a35e-528e-11e6-bb9d-f23c910a26c8/fbbe5cadd1c5afc73348fb2fa6b51c624953082e/files/foo.png
  #   View the file from within this object
  # GET /objects/bb79a35e-528e-11e6-bb9d-f23c910a26c8/fbbe5cadd1c5afc73348fb2fa6b51c624953082e/files/foo.png
  #   Optionally, place /objects in the front of the route.
  get %r{#{OBJECT_ROUTE_REGEX}(/?((?<tab>[^/]+)(?<path>.+)?)?)?$} do
    # Resolve the object a second time to set the path
    if params[:tab] == "raw" or params[:tab] == "files"
      @object = resolveObject()
    end

    format = request.preferred_type(['text/html', 'application/json', 'application/atom+xml', 'application/xml'])

    if params[:tab] == "raw"
      handleCORS('*')

      # Pull out file data
      meta = @object.retrieveFileStat(@object.path)

      if meta.nil?
        status 404
        return
      end

      cache_control :max_age => 31536000

      if meta[:type] == "tree"
        content_type "application/json"
        return @object.retrieveDirectory(@object.path).to_json
      end

      content_type((meta[:mime] || [])[0] || "application/octet-stream")

      if meta && meta.has_key?(:size)
        headers['X-Content-Length'] = meta[:size].to_s
      end

      return @object.retrieveFile(params[:path])
    end

    handleCORS()

    case format
    when 'application/json'
      content_type "application/json"

      if params[:tab] == "raw"
        # List file stat or directory stat
        stat = @object.retrieveFileStat(@object.path)
        if stat[:type] == "tree"
          @object.retrieveDirectory(@object.path).to_json
        else
          stat.to_json
        end
      elsif not params[:tab]
        @object.info.to_json
      end
    else
      layout = Occam::Layout.new(:view => :desktop,
                                 :tab  => params[:tab])

      if request.xhr? && params[:tab] == "files" && @object.path
        if @object.isGroup?(@object.path)
          render :haml, :"objects/_directory", :layout => false, :locals => {
            :layout => layout,
            :object => @object,
            :workset => nil
          }
        else
          render :haml, :"objects/_filedata", :layout => false, :locals => {
            :layout => layout,
            :object => @object,
            :data_mime_type => ""
          }
        end
      elsif request.xhr? && ["social", "files", "output", "access", "history"].include?(params[:tab])
        render :haml, :"objects/_#{params[:tab]}", :layout => !request.xhr?, :locals => {
          :layout => layout,
          :object => @object,
        }
      else
        render :haml, :"objects/show", :layout => !request.xhr?, :locals => {
          :layout => layout,
          :object => @object,
        }
      end
    end
  end

end
