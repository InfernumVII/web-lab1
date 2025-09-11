const numberRegex = new RegExp("^-?\\d+$")
function checkDecimal(text){
    return numberRegex.test(text)
}
function checkNumberBetween(a, b, number){
    return number >= -5 && number <= 3
}
function checkValueWithError(text){
    let errorText = ""
    let success = false
    if (!checkDecimal(text)){
        errorText = "Error: value should be number"
    } else {
        if (!checkNumberBetween(-5, -3, text)){
            errorText = "Error: value should be between -5 and 3"
        } else {
            success = true
        }
    }
    return [success, errorText]
}
function onInputTextUpdate(text){
    let validation = checkValueWithError(text)
    document.getElementById("input-text-error").innerHTML = validation[1];
    document.querySelector("#button-submit").disabled = !validation[0]
}

function submitOnClick(){
    var data = {
        x: parseInt(document.querySelector('input[name="X"]:checked').value),
        y: parseInt(document.querySelector('#input-y').value), 
        R: parseInt(document.querySelector('input[name="R"]:checked').value)
    }
    fetch("fcgi-app", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data),
    })
    .then((response) => response.json())
    .then((data) => {
        alert(data);//1
    })
    .catch((error) => {
       console.error(error); 
    });
}