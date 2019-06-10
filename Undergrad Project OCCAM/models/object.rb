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

class Occam
  class Object
    require 'date'
    require 'json'

    ALLOWED_COMMANDS = [
      'set',
      'view',
      'attach',
      'detach',
      'append',
      'configure'
    ]

    TEXT_MEDIA_TYPES = ['application/x-python',
                        'application/python',
                        'application/x-ruby',
                        'application/ruby',
                        'application/x-perl',
                        'application/perl',
                        'application/sh',
                        'application/x-sh',
                        'application/x-tex',
                        'application/tex',
                        'application/x-javascript',
                        'application/javascript',
                        'application/json']

    # The unique identifier for this object.
    attr_reader :uuid

    # The path selected within the object.
    attr_reader :path

    # The unique identifier for the root object
    attr_reader :withinUuid

    # The revision of the root object
    attr_reader :withinRevision

    # The local link
    attr_reader :link

    # The account that accessed the object
    attr_reader :account

    # The file requested within the object
    attr_reader :file

    # The index list of the object
    attr_reader :index

    # Creates an instance that represents the given Object.
    #
    # revision: The revision of the object we want to use.
    # file:     A file to pull from the object.
    # account:  The Account object that is attempting to view this object
    def initialize(options)
      @withinUuid = options[:uuid] || options[:id]
      @withinRevision = options[:revision]
      @index     = options[:index]
      @file      = options[:file]
      @account   = options[:account]
      @path      = options[:path]
      @info      = options[:info]
      @ownerInfo = options[:ownerInfo]
      @root      = options[:root]
      @roots     = options[:roots]
      @belongsTo = options[:belongsTo]
      @link      = options[:link]
      @schema    = nil
      @data      = nil
      @generated = nil

      @uuid = @withinUuid
      @revision = @withinRevision

      if options[:withinUuid]
        @withinUuid = options[:withinUuid]
      end

      if options[:withinRevision]
        @withinRevision = options[:withinRevision]
      end

      if @index and (not options[:withinUuid] or not options[:withinRevision] or not @roots)
        begin
          @uuid     = (@info || {})[:id]       || self.status[:id]
          @revision = (@info || {})[:revision] || self.status[:revision]

          @roots = @roots || self.status[:roots] || []
        rescue
        end
      end
    end

    def as(type, options = {})
      newOptions = {
        :uuid     => @withinUuid,
        :revision => @withinRevision,
        :path     => @path,
        :index    => @index,
        :link     => @link,
        :account  => @account,
        :info     => @info
      }

      newOptions.update(options)

      type.new(newOptions)
    end

    # Returns the url to the icon for this object.
    def iconURL(options={})
      # Detect if the icon exists for this type
      publicPath = File.join("public", "images", "icons", "objects", self.info[:type])

      basePath = "/images/dynamic"
      if options[:color]
        basePath = basePath + "/color/#{options[:color]}"
      elsif options[:hue]
        basePath = basePath + "/hue/#{options[:hue]}/sat/#{options[:sat]}/light/#{options[:light]}"
      elsif options[:hex]
        basePath = basePath + "/hex/#{options[:hex]}"
      else
        basePath = "/images"
      end

      if File.exists?(publicPath + ".svg")
        "#{basePath}/icons/objects/#{self.info[:type]}.svg"
      elsif File.exists?(publicPath + ".png")
        "#{basePath}/icons/objects/#{self.info[:type]}.png"
      else
        "#{basePath}/icons/objects/object.svg"
      end
    end

    # Returns the object that this one points to through "contents" relations.
    #
    # If you are accessing an object via a workset or group:
    #   /objects/3787d734-1bd6-11e7-affa-f23c910a26c8/c13ffacf0fe053f707a2b541da9b6f1fcd9e6b56/0
    #   that is: 3787d734-1bd6-11e7-affa-f23c910a26c8@c13ffacf0fe053f707a2b541da9b6f1fcd9e6b56[0]
    #
    # This will give you the object (for example):
    #   5ae90a98-23a3-11e7-b6fe-f23c910a26c8@876cdd8284e331ce95cc88f834309eec7ef4c530
    def resolve
      Occam::Object.new(:uuid => @uuid, :revision => @revision, :path => @path, :account => @account, :root => @root, :belongsTo => @belongsTo, :file => @file)
    end

    def contents(options={})
      if not self.info.has_key? :contains
        return []
      end

      if not self.info[:contains].is_a? Array
        return []
      end

      self.info[:contains].each_with_index.map do |contained, index|
        Occam::Object.new(:withinUuid => @withinUuid, :withinRevision => @withinRevision, :uuid => contained[:id], :revision => contained[:revision], :index => (@index || []) + [index], :roots => (@roots || []) + [self.info], :info => contained, :link => @link, :account => @account)
      end
    end

    # Returns a list of Authorship models connecting all Person records to Object records via "authors".
    def authorships
      if !defined?(@authorships)
        @authorships = (self.status[:authors] || []).map do |author|
          if author[:uid].nil?
            Occam::Authorship.new(:id => author[:id], :role => :author, :name => author[:name])
          else
            Occam::Authorship.new(:id     => author[:id],
                                  :role   => :author,
                                  :name   => author[:name],
                                  :person =>
              Occam::Person.new(:uuid => author[:uid], :info => {:name => author[:name], :id => author[:uid]})
            )
          end
        end
      end

      @authorships
    end

    # Returns a list of Authorship models connecting all Person records to Object records via "collaborators".
    def collaboratorships
      if !defined?(@collaboratorships)
        @collaboratorships = (self.status[:collaborators] || []).map do |collaborator|
          if collaborator[:uid].nil?
            Occam::Authorship.new(:id => collaborator[:id], :role => :collaborator, :name => collaborator[:name])
          else
            Occam::Authorship.new(:id     => collaborator[:id],
                                  :role   => :collaborator,
                                  :name   => collaborator[:name],
                                  :person =>
              Occam::Person.new(:uuid => collaborator[:uid], :info => {:name => collaborator[:name], :id => collaborator[:uid]})
            )
          end
        end
      end

      @collaboratorships
    end

    # Clears the permissions for the given Occam::Person.
    def resetPermission(children=false, person=nil)
    end

    # Sets permissions for the object and, optionally, for a particular Occam::Person.
    def setPermission(key, value, children=false, person=nil)
      arguments  = [self.fullID]
      cmdOptions = {}

      if children
        cmdOptions["-c"] = true
      end

      if @account
        cmdOptions["-T"] = @account.token
      end

      if !person.nil?
        arguments << person.fullID
      end

      if !key.nil?
        cmdOptions["-i"] = [[key]]
        if !value.nil?
          cmdOptions["-i"][0] << value
        end
      end

      result = Occam::Worker.perform("permissions", "set", arguments, cmdOptions)
      item = JSON.parse(result[:data], :symbolize_names => true)

      if item[:person]
        # Ensure we use the 'uuid' key when creating a local object
        # (occam reports uuids as 'id')
        item[:person][:uuid] = item[:person][:id]
        item[:person][:ownerInfo] = item[:person]
        item[:person][:info] = item[:person]
        item[:person][:account] = @account
        item[:person] = Occam::Person.new(item[:person])
      end

      item
    end

    # Retrieves the permission list for this object
    def permissions
      if !defined?(@permissions)
        arguments  = [self.fullID]
        cmdOptions = {}

        if @account
          cmdOptions["-T"] = @account.token
        end

        result = Occam::Worker.perform("permissions", "list", arguments, cmdOptions)
        data = JSON.parse(result[:data], :symbolize_names => true)

        base = {}

        if data.nil? or data.empty?
          data = {:object => [], :children => [], :reviewLinks => []}
        end

        @permissions = data

        [:object, :children].each do |key|
          @permissions[key].map do |item|
            if item[:person]
              item[:person].delete(:revision)
              item[:person][:ownerInfo] = item[:person]
              item[:person][:info] = item[:person]
              item[:person][:account] = @account
              item[:person] = Occam::Person.new(item[:person])
            else
              base[key] = item
            end
          end
        end

        # Create instances of the review links
        @permissions[:reviewLinks] =
          (@permissions[:reviewLinks] || []).map do |link|
            Occam::ReviewCapability.new(:id        => link[:id],
                                        :published => DateTime.iso8601(link[:published]),
                                        :object    => Occam::Object.new(:uuid     => link[:object][:id],
                                                                        :revision => link[:object][:revision],
								        :account  => @account))
          end

        # Ensure there is at least one children record with no :person
        # Ensure person-less entries are first
        [:object, :children].each do |key|
          if base[key].nil?
            base[key] = {}
            @permissions[key] << base[key]
          end

          @permissions[key].delete(base[key])
          @permissions[key].unshift(base[key])
        end
      end

      @permissions
    end

    def revision
      @revision || self.currentRevision
    end

    def ownerUUID
      self.ownerInfo[:id]
    end

    def parentRevision
    end

    def childRevisions
      []
    end

    def configurators
      []
    end

    def exists?
      !self.info.nil?
    end

    # Returns the revision known to this node as the canonical revision
    def currentRevision
      self.status[:currentRevision]
    end

    # Returns the revision from the node metadata for the root object.
    def rootRevision
      if @withinUuid == @uuid
        return self.currentRevision
      end

      if !defined?(@rootStatus)
        arguments  = ["#{@withinUuid}@#{@withinRevision}"]
        cmdOptions = {
          "-j" => true
        }

        if @account
          cmdOptions["-T"] = @account.token
        end

        result = Occam::Worker.perform("objects", "status", arguments, cmdOptions)
        @rootStatus = JSON.parse(result[:data], :symbolize_names => true)
      end

      @rootStatus[:currentRevision]
    end

    # Returns the node metadata for the stored object.
    def status
      if !defined?(@status)
        arguments  = [self.fullID]
        cmdOptions = {
          "-j" => true
        }

        if @account
          cmdOptions["-T"] = @account.token
        end

        result = Occam::Worker.perform("objects", "status", arguments, cmdOptions)
        if result[:code] != 0
          return nil
        end

        @status = JSON.parse(result[:data], :symbolize_names => true)
      end

      @status
    end

    # Returns the short identifier (uuid@revision)
    def shortID
      ret = @withinUuid

      if @withinRevision
        ret = "#{ret}@#{@withinRevision}"
      end

      if @link
        ret = "#{ret}##{@link}"
      end

      if @index
        @index.each do |position|
          ret = "#{ret}[#{position}]"
        end
      end

      ret
    end

    # Returns the full identifier for the object with an optional path.
    def fullID(path=nil)
      ret = self.shortID

      path = path || @file

      if path
        if path.start_with? "/"
          path = path[1..-1]
        end

        ret = "#{ret}/#{path}"
      end

      ret
    end

    # Returns the object information of the owning object as a Hash.
    def ownerInfo()
      # Conserve the result (it won't change)
      if !defined?(@ownerInfo) or @ownerInfo.nil?
        arguments  = [self.fullID(nil)]
        cmdOptions = {
          "-a" => true,
        }

        if @account
          cmdOptions["-T"] = @account.token
        end

        result = Occam::Worker.perform("objects", "view", arguments, cmdOptions)
        @ownerInfo = JSON.parse(result[:data], :symbolize_names => true)
      end

      @ownerInfo
    rescue
      nil
    end

    # Updates the given file with the given data
    #
    # If data is an array, it is set via keys. The contents of the array are
    # a set of key, value tuples.
    #
    # If you wish the values to be interpreted as json by the server, specify
    # 'json' as the :type parameter.
    #
    # If you wish to replace a file
    # completely with the contents of a hash, convert it directly to a json
    # string first and supply the string as the data parameter.
    def set(data, path = "object.json", options={})
      arguments  = [self.fullID(path)]
      cmdOptions = {
        "-j" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      stdinData = nil

      if data.is_a? Array
        data.each do |k, v|
          cmdOptions["-i"] ||= []
          
          if v.nil?
            cmdOptions["-i"] << [k]
          else
            cmdOptions["-i"] << [k, v]
          end
        end
      else
        stdinData = data
        arguments << "-"
      end

      cmdOptions["-t"] = options[:type] || "text"

      if options[:message]
        cmdOptions["-m"] = options[:message]
      end

      result = Occam::Worker.perform("objects", "set", arguments, cmdOptions, stdinData)
      data = JSON.parse(result[:data], :symbolize_names => true)

      root = nil
      ret = nil
      if data[:updated]
        root = data[:updated][0]
        index = data[:updated][1..-1].map do |item|
          item[:position]
        end

        ret = self.class.new(:uuid => root[:id], :revision => root[:revision], :index => index, :account => @account, :link => @link, :roots => data[:updated][0..-2], :root => root, :info => data[:updated][-1])
      end

      ret
    rescue Exception => e
      puts "Error saving"
      puts e
      nil
    end

    # Returns the object information as a Hash.
    def info(force = false)
      # Conserve the result (it won't change)
      if !defined?(@info) or @info.nil? or force
        info = self.ownerInfo
        if info.nil?
          return nil
        end

        if info[:id] == @uuid
          @info = self.ownerInfo
        else
          # Get the subinfo for the subobject
          arguments  = [self.fullID(nil)]
          cmdOptions = {
            "-a" => true,
            "--info" => true
          }

          if @account
            cmdOptions["-T"] = @account.token
          end

          result = Occam::Worker.perform("objects", "view", arguments, cmdOptions)

          @info = JSON.parse(result[:data], :symbolize_names => true)
        end
      end

      @info
    end

    def retrieveHistory
      # Conserve the result (it won't change)
      if !defined?(@commits)
        arguments  = [self.fullID(path)]
        cmdOptions = {
          "-j" => true,
        }

        if @account
          cmdOptions["-T"] = @account.token
        end

        result = Occam::Worker.perform("objects", "history", arguments, cmdOptions)
        @commits = JSON.parse(result[:data], :symbolize_names => true).map do |commit|
          begin
            commit[:date] = DateTime.iso8601(commit[:date])
          rescue
          end
          commit
        end
      end

      @commits
    end

    # Retrieves a syntax highlighted version of the given file within this object.
    def highlightedFile(path)
      arguments  = [self.fullID(path)]
      cmdOptions = {
        "-a" => true
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      # TODO: check for highlight module

      result = Occam::Worker.perform("analysis", "highlight", arguments, cmdOptions)
      result[:data]
    rescue
      nil
    end

    # Retrieves a file from the object store from the given path.
    def retrieveFileStat(path)
      arguments  = [self.fullID(path)]
      cmdOptions = {
        "-a" => true,
        "-j" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("objects", "status", arguments, cmdOptions)

      JSON.parse(result[:data], :symbolize_names => true)
    rescue
      nil
    end

    # Retrieves a file from the object store from the given path.
    def retrieveFile(path)
      arguments  = [self.fullID(path)]
      cmdOptions = {
        "-a" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("objects", "view", arguments, cmdOptions)
      result[:data]
    end

    # Lists the given directory from the object store.
    def retrieveDirectory(path)
      arguments  = [self.fullID(path)]
      cmdOptions = {
        "-a" => true,
        "-j" => true,
        "-l" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("objects", "list", arguments, cmdOptions)
      JSON.parse(result[:data], :symbolize_names => true)
    rescue
      []
    end

    # Retrieves a JSON document from the object store at the given path.
    #
    # Returns nil if there is any error
    def retrieveJSON(path)
      arguments  = [self.fullID(path)]
      cmdOptions = {
        "-a" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("objects", "view", arguments, cmdOptions)

      JSON.parse(result[:data], :symbolize_names => true)
    rescue
      nil
    end

    # Returns true if there is a 'run' section in this object.
    def runnable?
      self.info.has_key? :run
    end

    # Returns true if there is a 'build' section in this object.
    def buildable?
      self.info.has_key? :build
    end

    # Returns true if this object is a viewer.
    def viewer?
      self.info.has_key? :views
    end

    # Returns true if this object is trusted by the server or given person
    # to run directly on clients.
    def trusted?
      false
    end

    # Returns the list of parent objects which serve as the context for this
    # object.
    def parents
      ret = []

      current = self.parent

      while current
        ret << current
        current = current.parent
      end

      ret
    end

    # Returns the parent object within the navigation. The parent will have this
    # object in its "contains" list. This will be based on the index this
    # object has and its root.
    def parent
      if !defined?(@parent) or @parent.nil?
        @parent = nil
        if !@roots.nil? and @roots.length > 0
          @parent = Occam::Object.new(:uuid => @withinUuid, :revision => @withinRevision, :roots => @roots[0...-1], :ownerInfo => @roots[-1], :info => @roots[-1], :index => @index[0...-1], :account => @account, :link => @link)
        end
      end

      @parent
    end

    # Returns an Object instance for the Object that this object belongs to.
    #
    # Returns nil if this object doesn't belong to any other object.
    def belongsTo
      if !defined?(@belongsTo) or @belongsTo.nil?
        @belongsTo = self.info[:belongsTo]

        if !@belongsTo.nil?
          @belongsTo = Occam::Object.new(:uuid => belongsTo[:id], :revision => belongsTo[:revision], :account => @account)
        end
      end

      @belongsTo
    end

    def groups
      []
    end

    def experiments
      []
    end

    # Clones this object.
    #
    # Optionally, you can give an object to clone this object within. The given
    # object will contain the new fork.
    #
    # Returns: The Occam::Object for the brand new clone.
    def clone(to = nil, options = {})
      arguments  = [self.fullID]
      cmdOptions = {
        "-j" => true,
        "--internal" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      if to
        cmdOptions["--to"] = to.fullID
      end

      if options[:name]
        cmdOptions["--name"] = options[:name]
      end

      # TODO: provider path
      result = Occam::Worker.perform("objects", "clone", arguments, cmdOptions)
      data = JSON.parse(result[:data], :symbolize_names => true)

      root = nil
      ret = nil

      if data[:updated]
        root = data[:updated][0]
        index = data[:updated][1..-1].map do |item|
          item[:position]
        end

        ret = self.class.new(:uuid => root[:id], :revision => root[:revision], :index => index, :account => @account, :link => @link, :roots => data[:updated][0..-2], :root => root, :info => data[:updated][-1])
      end

      ret
    end

    # Pull out the root object
    def root
      if !defined?(@root) or @root.nil?
        @root = self.info[:root]

        if @root.nil?
          @root = self.info[:belongsTo]
        end

        if !@root.nil?
          @root = Occam::Object.new(:uuid => root[:id], :revision => root[:revision], :account => @account)
        end
      end

      @root
    end

    # Pull out the access parameters from the cached status
    def canEdit?
      (self.status[:access][:current] || {})[:write] == true
    end

    # Pull out the access parameters from the cached status
    def canView?
      (self.status[:access][:current] || {})[:read] == true
    end

    # Pull out the access parameters from the cached status
    def canClone?
      (self.status[:access][:current] || {})[:clone] == true
    end

    # Pull out the access parameters from the cached status
    def canExecute?
      (self.status[:access][:current] || {})[:execute] == true
    end

    # Returns a list of Object instances representing all possible viewers for
    # the current Object.
    def viewers(options = {})
      if !defined?(@viewers)
        arguments  = [self.fullID]
        cmdOptions = {
          "-j" => true,
        }

        if options[:subtype]
          cmdOptions["--subtype"] = options[:subtype]
        end

        if options[:type]
          cmdOptions["--type"] = options[:type]
        end

        if @account
          cmdOptions["-T"] = @account.token
        end

        result = Occam::Worker.perform("objects", "viewers", arguments, cmdOptions)
        ret = JSON.parse(result[:data], :symbolize_names => true)

        @viewers = ret.map do |info|
          Occam::Object.new(:info => info, :uuid => info[:id], :revision => info[:revision], :account => @account)
        end
      end

      @viewers
    end

    # Returns a list of Object instances representing all possible runners for
    # the current Object.
    def runners
    end

    def eachFile(path, options, &block)
      items = self.retrieveDirectory(path)
      items.each do |item|
        yield(item)
      end
    end

    def fileType(path)
      stat = self.retrieveFileStat(path)
      ((stat || {})[:mime] || ["application/octet-stream"])[0]
    end

    def isText?(path)
      if path.nil?
        return false
      end

      type = self.fileType(path)
      media_type = type.split("/")[0]

      if type && media_type == "text"
        true
      elsif type && TEXT_MEDIA_TYPES.include?(type)
        true
      elsif self.info[:subtype] == "text/plain" || (self.info[:subtype].is_a?(Array) && self.info[:subtype].include?("text/plain"))
      else
        # Query the analysis engine to see what type of file it is
        arguments  = [self.fullID(path)]
        cmdOptions = {
          "-a" => true
        }
        result = Occam::Worker.perform("analysis", "is-text", arguments, cmdOptions)
        JSON.parse(result[:data], :symbolize_names => true)[:result]
      end
    end

    def workflow
      {:connections => []}
    end

    def tail_connections
      []
    end

    def isWebImage?(path)
      return false if not self.isImage?(path)

      stat = self.retrieveFileStat(path)

      stat[:mime].select do |type|
        ["image/png", "image/x-icon", "image/gif", "image/jpeg"].include?(type)
      end.any?
    end

    def isImage?(path)
      if path.nil?
        return false
      end

      stat = self.retrieveFileStat(path)

      stat[:mime].select do |type|
        type.start_with? "image/"
      end.any?
    end

    def isBinary?(path)
      if path.nil?
        return false
      end

      extname = File.extname(path)[1..-1]
      type = nil

      if extname
        type = MIME::Types.type_for(extname).first
      end

      if type && type.binary?
        true
      else
        false
      end
    end

    def isGroup?(path)
      if (path.nil? || path == "" || path == "/")
        return true
      end

      # Pull the listing of the parent directory
      base = File.dirname(path)
      filename = File.basename(path)

      stat = self.retrieveDirectory(base)

      stat.each do |item|
        if item[:name] == filename
          if item[:type] == "tree" || item[:type] == "group"
            return true
          end
        end
      end

      false
    end

    # Retrieves the note data for the given category and possibly key
    def revisionsFor(category)
      arguments  = [self.fullID, category]
      cmdOptions = {
        "-j" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("notes", "list", arguments, cmdOptions)

      JSON.parse(result[:data], :symbolize_names => true)
    end

    # Retrieves the note data for the given category and possibly key
    def notes(category, key = nil)
      arguments  = [self.fullID, category]
      if key
        arguments << key
      end

      cmdOptions = {
        "-j" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("notes", "view", arguments, cmdOptions)

      JSON.parse(result[:data], :symbolize_names => true)
    end

    # Returns a list of possible backends that can run this object
    def backends
      arguments  = [self.fullID]
      cmdOptions = {
        "--backends" => true,
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("manifests", "run", arguments, cmdOptions)

      JSON.parse(result[:data], :symbolize_names => true)
    end

    # Returns True if this object can run on the given env/arch
    def supports?(environment, architecture)
      if self.info[:environment] == environment && self.info[:architecture] == architecture
        return true
      end

      arguments  = [self.fullID]
      cmdOptions = {
        "--target-architecture" => architecture,
        "--target-environment"  => environment,
        "--truth"               => true
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      # TODO: provider path
      result = Occam::Worker.perform("manifests", "run", arguments, cmdOptions)
      JSON.parse(result[:data], :symbolize_names => true)
    end

    # Returns a Hash of all tagged revisions for the object
    def versions
      arguments  = [self.fullID]
      cmdOptions = {
        "-j" => true
      }

      if @account
        cmdOptions["-T"] = @account.token
      end

      # TODO: provider path
      result = Occam::Worker.perform("objects", "tags", arguments, cmdOptions)
      JSON.parse(result[:data], :symbolize_names => true)
    end

    # Returns a list of Occam::Object for the included objects within this object.
    def includes
      if !defined?(@includes)
        @includes = nil
      end

      if @includes.nil?
        @includes = (self.info[:includes] || []).map do |info|
          Occam::Object.new(:account => @account, **info)
        end
      end

      @includes
    end

    # Returns a list of Occam::Object for the dependencies of this object.
    def dependencies(section = nil)
      if !defined?(@dependencies)
        @dependencies = {}
      end

      if !@dependencies.has_key?(section)
        subinfo = self.info

        if section
          subinfo = subinfo[section]
        end

        @dependencies[section] = (subinfo[:dependencies] || []).map do |info|
          Occam::Object.new(:account => @account, **info)
        end
      end

      @dependencies[section]
    end

    # Creates a link based on the given parameters.
    #
    # Returns: nil if the link cannot be created, or the link id if the link now exists.
    def createLink(options)
      cmdOptions = {}

      if @account
        cmdOptions["-T"] = @account.token
      end

      if options[:tracked]
        result = Occam::Worker.perform("links", "track", [options[:object].fullID], cmdOptions)
        cmdOptions["--track"] = result[:data].to_i.to_s
      end

      result = Occam::Worker.perform("links", "new", [self.fullID, options[:relationship], options[:object].fullID], cmdOptions)

      if defined?(@linksTo) && @linksTo.has_key?(options[:object].fullID)
        @linksTo.delete options[:object].fullID
      end
      if defined?(@links) && @links.has_key?(options[:relationship])
        @links.delete options[:relationship]
      end

      result[:data].to_i
    rescue
      nil
    end

    def destroyLink!(link)
      cmdOptions = {}

      if @account
        cmdOptions["-T"] = @account.token
      end

      Occam::Worker.perform("links", "delete", [self.fullID, link.relationship, link.target.fullID], cmdOptions)

      # Maintain Cache
      if defined?(@linksTo) && @linksTo.has_key?(link.target.uuid)
        @linksTo.delete link.target.uuid
      end
      if defined?(@links) && @links.has_key?(link.relationship)
        @links.delete link.relationship
      end
      true
    end

    # Returns a list of Links pointing to the given object
    def linksTo(object, options={})
      if !defined?(@linksTo)
        @linksTo = {}
      end

      arguments = [self.fullID]

      cmdOptions = {}
      cmdOptions["-j"] = true

      if @account
        cmdOptions["-T"] = @account.token
      end

      if options.has_key? :order
        cmdOptions["--order"] = options[:order].to_s
      end

      if !@linksTo.has_key?(object.uuid)
        cmdOptions["-d"] = object.fullID
        result = Occam::Worker.perform("links", "list", arguments, cmdOptions)

        ret = JSON.parse(result[:data], :symbolize_names => true)

        @linksTo[object.uuid] = {}
        ret.map do |link|
          if not @linksTo[object.uuid].has_key? link[:relationship]
            @linksTo[object.uuid][link[:relationship]] = []
          end
          @linksTo[object.uuid][link[:relationship]] <<
            Occam::Link.new(:source => self,
                            :target => Occam::Object.new(:uuid => link[:id], :info => link, :revision => link[:revision], :link => link[:local_link_id], :account => @account),
                            :relationship => link[:relationship],
                            :local_link_id => link[:local_link_id],
                            :account => @account,
                            :id => link[:id])
        end
      end

      @linksTo[object.uuid]
    end

    # Returns a list of Links for the given relationship
    def links(relationship, options = {})
      if !defined?(@links)
        @links = {}
      end

      arguments = [self.fullID]

      cmdOptions = {}
      cmdOptions["-j"] = true

      if @account
        cmdOptions["-T"] = @account.token
      end

      cmdOptions["-r"] = relationship

      if options.has_key? :order
        cmdOptions["--order"] = options[:order].to_s
      end

      if !@links.has_key?(relationship) || options[:force]
        result = Occam::Worker.perform("links", "list", arguments, cmdOptions)
        ret = JSON.parse(result[:data], :symbolize_names => true)

        @links[relationship] = ret.map do |link|
          Occam::Link.new(:source => self,
                          :target => Occam::Object.new(:uuid => link[:id], :info => link, :revision => link[:revision], :link => link[:local_link_id], :account => @account),
                          :relationship => relationship,
                          :local_link_id => link[:local_link_id],
                          :account => @account,
                          :id => link[:id])
        end
      end

      @links[relationship]
    end

    def qrcode_url(request, options = {})
      image_data = self.qrcode(request, options)

      if options[:type] == :png
        image_data = Base64.encode64(image_data)
        "data:image/png;base64,#{image_data}"
      else
        "data:image/svg+xml;charset=UTF-8,#{image_data}"
      end
    end

    def qrcode(request, options = {})
      url = "#{request.scheme}://#{request.host_with_port}#{self.url}"
      qrcode = RQRCode::QRCode.new(url)

      if options[:type] == :svg
        qrcode.as_svg(:offset          => options[:offset]         || 0,
                      :shape_rendering => options[:shapeRendering] || "crispEdges",
                      :module_size     => options[:moduleSize]     || 11)
      else # default == :png
        qrcode.as_png(:size            => options[:size]           || 240,
                      :fill            => options[:fill]           || 'white',
                      :color           => options[:color]          || 'black',
                      :border_modules  => options[:borderModules]  || 4,
                      :module_px_size  => options[:modulePxSize]   || 6).to_s
      end
    end

    def url(options = {})
      require 'uri'

      # Returns the local url for this Object
      ret = "/#{@withinUuid}"

      if @withinRevision
        ret = "#{ret}/#{@withinRevision}"
      else
        ret = "#{ret}/#{self.rootRevision}"
      end

      if @index
        @index.each do |index|
          ret = "#{ret}/#{index}"
        end
      end

      if options[:path]
        ret = "#{ret}/#{options[:path]}"
      end

      if @link
        options[:query] = options[:query] || {}

        options[:query][:link] = @link
      end

      if options[:query]
        query = URI.encode_www_form(options[:query])
        ret = "#{ret}?#{query}"
      end

      ret
    end

    def configurations
      []
    end

    def datapoints?
      false
    end

    def review_capabilities
      self.permissions[:reviewLinks]
    end

    # Creates a new object within this object.
    def create(options)
      arguments = [options[:type], options[:name]]

      cmdOptions = {}
      cmdOptions["--internal"] = true
      cmdOptions["-j"] = true
      cmdOptions["--to"] = self.fullID

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("objects", "new", arguments, cmdOptions)
      data = JSON.parse(result[:data], :symbolize_names => true)

      root = nil
      ret = nil

      if data[:updated]
        root = data[:updated][0]
        index = data[:updated][1..-1].map do |item|
          item[:position]
        end

        ret = Occam::Object.new(:uuid => root[:id], :revision => root[:revision], :index => index, :account => @account, :link => @link)

        # if the type is an experiment, also create a workflow
        if options[:type] == "experiment"
          workflow = Occam::Workflow.createIn(ret, :name => "Main", :account => @account)
          puts workflow
          ret = Occam::Object.new(:uuid => root[:id], :revision => workflow.withinRevision, :index => index, :account => @account, :link => @link)
        end
      end

      ret
    end

    # Queues a run of the object and returns an Occam::Job
    def run(options={})
      cmdOptions = {}
      if @account
        cmdOptions["-T"] = @account.token
      end

      if self.info[:type] == "workflow" or self.info[:type] == "experiment"
        # Ok, the result should be any other tasks that must be performed...
        result = Occam::Worker.perform("workflows", "queue", [self.fullID], cmdOptions)
        job = JSON.parse(result[:data], :symbolize_names => true)[:job]
      else
        cmdOptionsA = cmdOptions.dup
        cmdOptionsA["--task-only"] = true

        if options[:input]
          cmdOptionsA["--input"] = options[:input].shortID
        end

        result = Occam::Worker.perform("objects", "run", [self.fullID], cmdOptionsA)
        data = JSON.parse(result[:data], :symbolize_names => true)

        task = Occam::Object.new(:uuid => data[:id], :revision => data[:revision], :account => @account)

        cmdOptionsB = cmdOptions.dup
        cmdOptionsB["-j"] = true

        if options[:interactive] == true
          cmdOptionsB["-i"] = true
        end

        result = Occam::Worker.perform("jobs", "queue", [task.fullID], cmdOptionsB)
        job = JSON.parse(result[:data], :symbolize_names => true)
      end

      Occam::Job.new(:id => job[:id])
    end

    # Queues a build of the object and returns an Occam::Job
    def build(options={})
      cmdOptions = {}
      if @account
        cmdOptions["-T"] = @account.token
      end

      cmdOptionsA = cmdOptions.dup
      cmdOptionsA ["--task-only"] = true
      result = Occam::Worker.perform("objects", "build", [self.fullID], cmdOptionsA)

      data = JSON.parse(result[:data], :symbolize_names => true)

      task = Occam::Object.new(:uuid => data[:id], :revision => data[:revision], :account => @account)

      cmdOptionsB = cmdOptions.dup
      cmdOptionsB ["-j"] = true
      result = Occam::Worker.perform("jobs", "queue", [task.fullID], cmdOptionsB)
      data = JSON.parse(result[:data], :symbolize_names => true)

      Occam::Job.new(:id => data[:id])
    end

    # Returns a list of objects generated as output
    def generated
      if @generated.nil?
        generated = self.notes("outputs", "object") || []

        @generated = generated.map do |object|
          Occam::Object.new(object[:value].update(:info => object[:value], :account => @account))
        end
      end

      @generated
    end

    # Returns the schema attached to this object.
    def schema
      if @schema.nil?
        if info[:schema]
          if info[:schema].is_a? String
            @schema = self.retrieveJSON(info[:schema])
          else
            schemaObject = Occam::Object.new(:uuid     => info[:schema][:id],
                                             :revision => info[:schema][:revision],
                                             :file     => info[:schema][:file],
					     :account  => @account)
            @schema = schemaObject.retrieveJSON(schemaObject.file)
          end
        end
      end

      @schema
    end

    # Returns the data associated with this object.
    def data
      if @data.nil?
        file = @file || self.info[:file]
        if file
          @data = self.retrieveJSON(file)
        end
      end

      @data
    end

    def citation(options={})
      options[:format] ||= "bibtex"
      options[:output] ||= "html"

      arguments = [self.fullID]

      cmdOptions = {
        "-f" => options[:format],
        "-o" => options[:output]
      }

      result = Occam::Worker.perform("citations", "view", arguments, cmdOptions)
      result[:data]
    end

    # Creates a new object with the given options.
    def self.create(options)
      arguments = [options[:type], options[:name]]

      cmdOptions = {}
      cmdOptions["-j"] = true
      cmdOptions["--internal"] = true

      if options[:account]
        cmdOptions["-T"] = options[:account].token
      end

      result = Occam::Worker.perform("objects", "new", arguments, cmdOptions)
      data = JSON.parse(result[:data], :symbolize_names => true)
      Occam::Object.new(:uuid => data[:updated][0][:id], :revision => data[:updated][0][:revision], :account => options[:account])
    rescue
      nil
    end

    def self.associationFor(options)
      false
    end

    def self.types(options)
      query = ""
      if options[:query]
        query = options[:query]
      end

      cmdOptions = {}
      cmdOptions["-j"] = true
      cmdOptions["--types"] = query

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("objects", "search", [], cmdOptions)

      JSON.parse(result[:data], :symbolize_names => true)
    end

    # Adds a comment on the backend for this object.
    def addComment(options)
      arguments  = [self.fullID, options[:comment]]
      cmdOptions = {}

      if @account
        cmdOptions["-T"] = @account.token
      end

      if options[:anon]
        cmdOptions["-a"] = true
      end

      if options[:inReplyTo] and options[:inReplyTo] != ""
        cmdOptions["--reply-to"] = options[:inReplyTo]
      end

      result = Occam::Worker.perform("social", "comment", arguments, cmdOptions)
      JSON.parse(result[:data], :symbolize_names => true)
    rescue
      {}
    end

    # Mark the specified comment as deleted. This does not modify the content.
    def deleteComment(options)
      arguments  = [options[:comment_id]]
      cmdOptions = {}

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("social", "delete-comment", arguments, cmdOptions)
      JSON.parse(result[:data], :symbolize_names => true)
    rescue
      {}
    end

    # Update the specified comment. Creates a shadow comment in the backend
    # with the previous content.
    def editComment(options)
      arguments  = [options[:edit], options[:comment]]
      cmdOptions = {}

      if @account
        cmdOptions["-T"] = @account.token
      end

      result = Occam::Worker.perform("social", "modify-comment", arguments, cmdOptions)
      JSON.parse(result[:data], :symbolize_names => true)
    rescue
      {}
    end

    # Show comments for this object based on the settings passed in.
    def viewComments(options = {})
      arguments  = [self.fullID]
      cmdOptions = {}

      if @account
        cmdOptions["-T"] = @account.token
      end

      # If the backend should return a single comment (still an array).
      if options[:only_matching]
        cmdOptions["--only-matching"] = true
      end

      # We are pulling comments specifically in reply to the given comment
      if options[:inReplyTo]
        cmdOptions["--reply-to"] = options[:inReplyTo]
      end

      # We are pulling comments for the given identifier
      if options[:id]
        arguments << options[:id]

        # Also pull parent comments
        cmdOptions["-c"] = true
      end

      # Give all comments for this object.
      if options[:all]
        cmdOptions["-a"] = true
      end

      # Further limit comments to those made after a certain date.
      if options[:since]
        cmdOptions["-s"] = options[:since]
      end

      result = Occam::Worker.perform("social", "comment-list", arguments, cmdOptions)
      comments = JSON.parse(result[:data], :symbolize_names => true)

      commentMap = {}

      ret = []
      comments.map do |item|
        # Ensure replies is at least an empty array
        item[:replies] ||= []

        if commentMap.has_key? item[:id]
          item[:replies] = commentMap[item[:id]][:replies]
        end
        commentMap[item[:id]] = item

        if (not options[:only_matching]) && item[:inReplyTo] && (options[:inReplyTo].nil? || item[:inReplyTo] != options[:inReplyTo])
          commentMap[item[:inReplyTo]] ||= {}
          commentMap[item[:inReplyTo]][:replies] ||= []
          commentMap[item[:inReplyTo]][:replies] << item
        else
          ret << item
        end

        if not options[:simple]
          begin
            item[:person].delete(:revision)
            item[:person][:ownerInfo] = item[:person]
            item[:person][:info] = item[:person]
            item[:person][:account] = @account
            item[:person] = Occam::Person.new(item[:person])
          rescue
            item.delete :person
          end
        end

        item
      end

      ret
    rescue
      []
    end

    #1980
    def undefinedKeys()
      [:name, :collaborators, :website, :source, :backed, :generator, :configures, :views, :configuration, :environment, :architecture].select do |key|
        !self.info.keys.include?(key)
      end
    end

    def self.search(options)
      arguments  = []
      cmdOptions = {}

      if options[:name]
        cmdOptions["--name"] = options[:name]
      end

      if options[:type]
        cmdOptions["--type"] = options[:type]
      end

      if options[:uuid]
        cmdOptions["--uuid"] = options[:uuid]
      end

      if @account
        cmdOptions["-T"] = @account.token
      end

      cmdOptions["-j"] = true

      result = Occam::Worker.perform("objects", "search", arguments, cmdOptions)
      ret = JSON.parse(result[:data], :symbolize_names => true)

      ret.map do |info|
        Occam::Object.new(:info => info, :uuid => info[:id], :revision => info[:revision], :account => @account)
      end
    end
  end
end
