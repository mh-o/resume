## This is a copy of the edits made during our time working on OCCAM UI. It's a large ongoing project but myself and my team were responsible for a small amount of UI changes tracked in the code below. The public code is available at https://gitlab.com/occam-archive/occam

# OCCAM_edits
### Details Page
What **should** happen
* Each editable key will have a pencil icon to the right of it. Clicking this icon brings up a form that will edit that key. Multiple keys can be edited at once
* For keys not yet set, the dropdown at the bottom should bring up another row for that key once a key has been selected. After the changes are made, that key should appear in its proper location after the page refresh

### Files Page
What **should** happen
* Dynamic file uploads were working... but then drag and drop messed with them. I have to check with my code before this is tested
* Drag and drop right now will create a new file under the proper name, but this will screw up the entire object. If you want to play with it for fun you can, but note you'll have to create a new object if you want to test anything else


**THE FOLLOWING FILES HAVE BEEN EDITED:**
* config -> locales -> en.yml
* controllers -> objects.rb
* models -> object.rb
* public -> images -> ui -> edit.svg
* public -> js -> drag_drop.js
* public -> js -> object_edit.js
* views -> objects -> _filelist.haml
* views -> objects -> _information.haml
* views -> stylesheets -> _card.scss
* views -> _scripts.haml
