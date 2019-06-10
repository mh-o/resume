function editElement(div1, div2, div3) {
  document.getElementById(div1).hidden = true;
  document.getElementById(div2).hidden = false;
  document.getElementById(div3).hidden = true;
}
function setElement(element) {
  var new_element = document.getElementById(element).elements[0].value;
}
function dropDown() {
  var parent = document.getElementById("drop_env")
  var my_key = document.getElementById("element_select").value;

  var x = document.createElement("input");
  x.setAttribute("type", "text");
  x.setAttribute("name", my_key)

  document.getElementById("old_element").hidden = true;
  parent.appendChild(x);
  parent.hidden = false;

}

var links = document.querySelectorAll(".information a.edit");

links.forEach(function(element) {
  element.addEventListener("click", function(event) {
    element.parentNode.hidden = true;
    element.parentNode.nextElementSibling.hidden = false;
  });
});

var dropDown = document.querySelector(".information select.options");

if(dropDown) {
  dropDown.addEventListener("change", function(event) {
    var item = dropDown.value;
    var row = document.querySelector(".information > ." + item);
    var insertTo = document.querySelector(".information .other-keys");

    insertTo.parentNode.insertBefore(row, insertTo);
    row.hidden = false;
  });
}
