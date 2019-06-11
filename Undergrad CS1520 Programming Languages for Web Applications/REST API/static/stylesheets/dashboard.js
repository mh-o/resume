if(!!window.performance && window.performance.navigation.type === 2) {
    window.location.reload();
}

document.addEventListener("DOMContentLoaded", function(event) {
  var colors = [
      [
          'mdl-color--blue-500',
          'mdl-color--blue-500',
          'mdl-color--blue-500',
          'mdl-color--blue-500',
          'mdl-color--blue-500',
          'mdl-color--blue-500'
      ],
      [
          'mdl-color--blue-500',
          'mdl-color--blue-500',
          'mdl-color--blue-500',
          'mdl-color--blue-500',
          'mdl-color--blue-500',
          'mdl-color--blue-500'
      ]
  ];

    var rentUtils_progress_container = document.querySelector('.graph_holder');
    var openButton = document.getElementsByClassName('my-fab')[0];
    var newCatNameField = document.getElementById('new_cat_name_field');
    var newCatLimitField = document.getElementById('new_cat_limit_field');
    var createButton = document.getElementById('create_dialog');
    var dismissButton = document.getElementById('dismiss_dialog');
    var dialog = document.querySelector('dialog');

    newCatNameField.addEventListener('input', isValidForm);
    newCatLimitField.addEventListener('input', isValidForm);

    openButton.addEventListener('click', function() {
        dialog.showModal();
    });

    createButton.addEventListener('click', function(){
        addCategory(newCatNameField.value, newCatLimitField.value);
        dialog.close();
        newCatNameField.value = '';
        newCatLimitField.value = '';

        (newCatNameField.parentNode).MaterialTextfield.checkDirty();
        (newCatNameField.parentNode).MaterialTextfield.change();

        (newCatLimitField.parentNode).MaterialTextfield.checkDirty();
        (newCatLimitField.parentNode).MaterialTextfield.change();
    });

    dismissButton.addEventListener('click', function(){
        dialog.close();

        newCatNameField.value = '';
        newCatLimitField.value = '';

        (newCatNameField.parentNode).MaterialTextfield.checkDirty();
        (newCatNameField.parentNode).MaterialTextfield.change();

        (newCatLimitField.parentNode).MaterialTextfield.checkDirty();
        (newCatLimitField.parentNode).MaterialTextfield.change();
    });

    requestAllCategories(initAllCategories);

    function requestAllCategories(callback) {
        var xhr = new XMLHttpRequest();

        xhr.onload = function() {
            if (xhr.readyState == 4 && xhr.status == 200){
                console.log("GET: CATEGORIES: /cats");
                console.log('GOT: ');
                console.log(JSON.parse(xhr.responseText));
                console.log("________________________");
                callback(JSON.parse(xhr.responseText));
            }
            else
                huh('Failed to Fetch Categories')
        };
        xhr.open("GET", '/cats', true);
        xhr.send(null);

        function huh(message){

                var handler = function(event) {};
                var snackbarContainer = document.querySelector('#demo-snackbar-example');
                var data = {
                    message: message,
                    timeout: 2500,
                    actionHandler: handler,
                    actionText: 'OK'
                };

                snackbarContainer.MaterialSnackbar.showSnackbar(data);
        }

    }

    function initAllCategories(rawJson){
        var allCats = rawJson['categories'];

        var temp = 0;
        allCats.forEach(function(item){
            if(temp == 0){
                // Handle rentUtils
                temp = 1;
                var rentUtils = document.getElementById('card_frame');
                rentUtils.querySelector('.card-limit-label').textContent = item['cost'];
                rentUtils.querySelector('a').addEventListener('click', function(){
                    document.getElementById('cat_rentUtils').submit();
                });
            }
            else {
                addNewCard(item['name'], item['cost']/item['limit'], item['cost'], item['limit']);
            }
        })

    }

    /**
     *    Validates form before attempting POST in order to create new Category
     */
    function isValidForm(){

        if(newCatNameField.value.length > 0 && checkDuplicateName(newCatNameField.value) &&
            newCatLimitField.validity.valid && newCatLimitField.value.length > 0){
            createButton.classList.remove('mdl-button--disabled');
        }
        else {
            createButton.classList.add('mdl-button--disabled');
        }
    }

    /**
     *  Checks for redundant Category Creation
     *
     * @param proposedName  The current name in the form before submitting
     * @returns {boolean}   indicates whether category name already exists
     */
    function checkDuplicateName(proposedName){
        var allCatNames = document.querySelectorAll('h4');

        for (var i = 0; i < allCatNames.length; i++) {
          if(allCatNames[i].textContent == proposedName)
              return false;
        }

        return true
    }

    /**
     *  Builds a new card by cloning default rentUtils purchases card and populating
     *  specified values to match the category
     *
     * @param cat_name  the name of category being added
     * @param percent   the percentage of specified budget
     */
    function addNewCard(cat_name, percent, cost, limit){
        var parent = document.getElementById('cat_holder');
        var cloned_card = document.getElementById('card_frame').cloneNode(true);
        cloned_card.firstElementChild.classList.remove('mdl-color--grey');
        cloned_card.firstElementChild.classList.add(colors[Math.round(Math.random())][Math.floor(Math.random()*6)]);
        var f = cloned_card.querySelector('form');
        f.querySelector('input').setAttribute('value', cat_name);

        f.querySelector('a').addEventListener('click', function(){
           f.submit();
        });

        cloned_card.querySelector('h4').firstChild.textContent = cat_name;
        cloned_card.querySelector('.card-limit-label').textContent = cost+" / "+limit;

        parent.appendChild(cloned_card);

        setProgress(cloned_card.querySelector('.graph_holder'), percent);
    }

    /**
     * Initlizes the progress bar of a category card
     *
     * @param container the specfic graph container to be populated
     * @param progress  the percentage of limit (specify with floats)
     */
    function setProgress(container, progress){
        var bar = new ProgressBar.SemiCircle(container, {
            strokeWidth: 6,
            color: '#fff',
            trailColor: '#e0e0e0',
            trailWidth: 1,
            easing: 'easeInOut',
            duration: 1400,
            svgStyle: null,
            text: {
                value: '',
                alignToBottom: false
            },
            from: {color: '#fff'},
            to: {color:'#fff'},
             // Set default step function for all animate calls
            step: (state, bar) => {
                bar.path.setAttribute('stroke', state.color);
                var value = Math.round(bar.value() * 100);
                if (value === 0) {
                    bar.setText('0%');
                } else {
                    bar.setText(value+"%");
                }
            bar.text.style.color = state.color;
          }
        });
        bar.text.style.fontFamily = '"Roboto", Helvetica, sans-serif';
        bar.text.style.fontSize = '2rem';

        bar.animate(progress);  // Number from 0.0 to 1.0
    }


    /**
     *  Sends a post to the /cats endpoint in order to create a new category
     *
     * @param categoryName  the name of the category whos creation is being requested
     * @param categoryLimit the limit of the category whos creation is being requested
     */
    function addCategory(categoryName, categoryLimit){

        if(categoryName.length > 0){
            var cat_object = JSON.stringify(
                {
                    new_cat_name: categoryName,
                    new_cat_limit: categoryLimit
                }
            );

            var xhr = new XMLHttpRequest();
            xhr.open("POST", '/cats', true);
            xhr.setRequestHeader('Content-Type', 'application/json');

            xhr.onload = function() {
                var res = JSON.parse(xhr.responseText);
                if(xhr.status !== 200){
                    signal("Network Error:"+xhr.status);
                }
                if(res["completed"]){
                    signal('New category was created');
                    console.log("NEW CATEGORY, POST: /cats");
                    console.log('SENT: ');
                    console.log(cat_object);
                    console.log('GOT: ');
                    console.log(res);
                    console.log("_________________________");

                    addNewCard(categoryName,.0, 0, categoryLimit)
                }
                else {
                    signal('Failed to create new category');
                }
            };

            xhr.send(cat_object);

            function signal(message){

                var handler = function(event) {};
                var snackbarContainer = document.querySelector('#demo-snackbar-example');
                var data = {
                    message: message,
                    timeout: 2500,
                    actionHandler: handler,
                    actionText: 'OK'
                };

                snackbarContainer.MaterialSnackbar.showSnackbar(data);
            }
        }
    }
});
