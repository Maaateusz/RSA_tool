
var loader = null;
var generate_url = "/generatersa";
var encrypt_url = "/encrypt";
var decrypt_url = "/decrypt";
var http_request = null;

function init(){
    loader = document.getElementById("loader");
    loader.style.display = "none";
}

function generate_RSA() {
    if (window.XMLHttpRequest) {
        // code for modern browsers
        http_request = new XMLHttpRequest();
    } else {
        // code for IE6, IE5
        http_request = new ActiveXObject("Microsoft.XMLHTTP");
    }
    http_request = new XMLHttpRequest();
    http_request.onreadystatechange = get_response_from_generate_RSA;
    http_request.open("GET", generate_url, true); //true, określa, że żądanie ma być asynchroniczne
    http_request.send(null);
    loader.style.display = "block";
}

function get_response_from_generate_RSA() {
    if (http_request.readyState == 4) { //4 = żądanie zakończone; 2 = zapytanie wysłane; 3 = odbieranie odpowiedzi
        loader.style.display = "none";
        if (http_request.status == 200) {
            var json_response = JSON.parse(http_request.responseText);
            var RSAkey = json_response.RSAkey;
            document.getElementById("generated_date_text").innerHTML = RSAkey.create_date;
            document.getElementById("public_key_text").innerHTML = RSAkey.public_key;
            document.getElementById("private_key_text").innerHTML = RSAkey.private_key;
            document.getElementById("key_lenght_text").innerHTML = RSAkey.key_lenght;
            textarea_autosize();
        } else {
            alert('ERROR');
        }
        http_request = null;
    }
}

function encrypt(){
    if (window.XMLHttpRequest) {
        http_request = new XMLHttpRequest();
    } else {
        http_request = new ActiveXObject("Microsoft.XMLHTTP");
    }
    http_request = new XMLHttpRequest();
    http_request.onreadystatechange = get_response_from_encrypt;
    http_request.open("POST", encrypt_url, true);
    http_request.setRequestHeader("Content-type", "application/json");
    var public_key = document.getElementById("public_key").value;
    var pre_data = document.getElementById("pre_data").value;
    var data = JSON.stringify({"public_key": public_key, "data": pre_data});
    http_request.send(data);
}

function get_response_from_encrypt() {
    if (http_request.readyState == 4) {
        loader.style.display = "none";
        if (http_request.status == 200) {
            //var json_response = JSON.parse(http_request.responseText);
            var encrypted_data = http_request.responseText;
            //alert(encrypted_data);
            document.getElementById("encrypted_data").value = encrypted_data;
        } else {
            alert('ERROR');
        }
        http_request = null;
    }
}

function decrypt(){
    if (window.XMLHttpRequest) {
        http_request = new XMLHttpRequest();
    } else {
        http_request = new ActiveXObject("Microsoft.XMLHTTP");
    }
    http_request = new XMLHttpRequest();
    http_request.onreadystatechange = get_response_from_decrypt;
    http_request.open("POST", decrypt_url, true);
    http_request.setRequestHeader("Content-type", "application/json");
    var private_key = document.getElementById("private_key").value;
    var post_data = document.getElementById("post_data").value;
    var data = JSON.stringify({"public_key": private_key, "data": post_data});
    http_request.send(data);
}

function get_response_from_decrypt() {
    if (http_request.readyState == 4) {
        loader.style.display = "none";
        if (http_request.status == 200) {
            //var json_response = JSON.parse(http_request.responseText);
            var decrypted_data = http_request.responseText;
            //alert(decrypted_data);
            document.getElementById("decrypted_data").value = decrypted_data;
        } else {
            alert('ERROR');
        }
        http_request = null;
    }
}

//---- auto size textarea ----//
function textarea_autosize() {
   const tx = document.getElementsByTagName('textarea');
    for (let i = 0; i < tx.length; i++) {
      tx[i].setAttribute('style', 'height:' + (tx[i].scrollHeight) + 'px;overflow-y:hidden;');
      tx[i].addEventListener("input", textarea_autosize, false);
  }
  //this.style.height = 'auto';
  this.style.height = (this.scrollHeight) + 'px';
}
//---- auto size textarea ----//

window.onload = function(){
    init();
};
