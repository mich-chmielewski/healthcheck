// Scripts

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

});



function getMainContent(page , postData=null) {
    if (page === null) page = 'dashboard';
    if (event) event.preventDefault();
    window.history.replaceState("","Loaded Page",page);
    let url = '/view/' + page.toLowerCase();
    isLogged();
    $.post(url,postData,function(data, textStatus, jqXHR) {
        $('#replacedMain').html(data);
        delete table;
        if($('#simpleTable').length){
          table = $('#simpleTable').DataTable({
           responsive: true,
           ordering: false
          });
        }
        initDatePicker();
    }).fail(function(jqXHR){
       showAlert("Error!", jqXHR.responseJSON.error, jqXHR.status);
    });
};


function initDatePicker(){

    $('#datepicker')
    .datepicker({
                    format: 'yyyy-mm-dd',
                    autoclose: true,
                    todayHighlight: true
                })
    .on('changeDate', function(ev){
            let dateString = ev.date.toISOString().substring(0,10);
            getMainContent('hitlog' , JSON.parse('{"fromday":"'+dateString+'"}'));
    });
}


function getJsonFromForm(formId) {
  var params = {};
  $('#'+formId +' input, #' + formId + ' select, #'+ formId +' textarea, #' + formId +' checkbox').each(
      function(index){
          let input = $(this);
          if (input[0].type === "checkbox" && input[0].checked) {
            params[input.attr('name')] = true;
            return;
          }
          params[input.attr('name')] = input.val();
      }
  );
  return JSON.stringify(params);
}

function hideModal(){
  $('.modal-backdrop').remove();
  $('.modal').modal('hide');
}

function showAlert(msgTitle, msgBody, msgType) {
    $('#alertMsg').find('strong').html(msgTitle + " Status: " + msgType);
    $('#alertMsg').find('p').html(msgBody);
    $('#alertMsg').show();
}

/* User operations */

async function isLogged() {
    let url = '/verify';
    fetch(url,{ method: 'POST', redirect:'follow'})
        .then(response => {
            if (response.redirected === true)
                window.location.href = "/login?error";

            response.json().then(value  => {
             if (value === false)
                window.location.href = "/login?error";
            });
        })
        .catch(function(err) {
                 console.info(err + " url: " + url);
        });
}

/* ServiceUrl operations */

function addServiceModal(){
  $('#serviceUrlForm' ).trigger("reset");
  $('#operation').val('post');
  $('#serviceModal').modal('show');
};

function sendServiceForm(){
  event.preventDefault();
  let formData = getJsonFromForm('serviceUrlForm');
    $.ajax({
        type: $('#operation').val(),
        crossDomain: true,
        url: "/api/service/",
        data: formData,
        contentType: "application/json"
    })
    .done(function(data, textStatus, jqXHR){
      hideModal();
      getMainContent('services');
    })
    .fail(function(jqXHR, error, message){
       hideModal();
       showAlert(error, jqXHR.responseJSON.message, jqXHR.status);
    });
   };

async function getServiceUrl(id) {
    event.preventDefault();
    let url = '/api/service/' + id;
    let response = await fetch(url);
    if (response.status === 200) {
        let data = await response.json();
        $('#id').val(data['id']);
        $('#operation').val('put');
        $('#urlAddress').val(data['urlAddress']);
        $('#responseType').val(data['responseType'].toString()).change();
        $('#requestSchedule').val(data['requestSchedule'].toString()).change();
       // $('#enebled').val(data['enebled'].toString()).change();
        $('#serviceModal').modal('show');
    }
};

function deleteServiceUrl(id) {
    event.preventDefault();
    bootbox.confirm("Confirm delete operation?", function(result) {
     if(result){
        let url = '/api/service/' + id;
        fetch(url,{
           method: 'DELETE',
           headers: {
             'Content-Type': 'application/json'
           },
           body: null
           })
            .then((response) => {
              if (response.ok) {
                return getMainContent('services');
              }
              return Promise.reject(response);
            })
            .catch((response) => {
                response.json().then((json) => {
                    showAlert('Error', json.message, json.code);
                })
            });
        }
    });
};

function deleteAllHitLogs() {
    event.preventDefault();
    bootbox.confirm("Confirm delete operation?", function(result) {
     if(result){
        let url = '/api/hitlogs/';
        fetch(url,{
           method: 'DELETE',
           headers: {
             'Content-Type': 'application/json'
           },
           body: null
           })
            .then((response) => {
              if (response.ok) {
                return getMainContent('hitlog');
              }
              return Promise.reject(response);
            })
            .catch((response) => {
                response.json().then((json) => {
                    showAlert('Error', json.message, json.code);
                })
            });
        }
    });
};
