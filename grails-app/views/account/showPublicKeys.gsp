<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>Account Settings</h1>

<div class="tabbable tabs-left">

    <g:render template="tabs" model="[active: 'ssh']"/>

    <div class="tab-content span5">

        <div class="accordion" id="keys"></div>

        <g:formRemote name="sshForm" url="[mapping: 'account_ssh']"
                      onSuccess="addSuccess(data)"
                      onFailure="fieldError(XMLHttpRequest)">
            <fieldset>
                <div class="control-group">
                    <label for="title">Title</label>

                    <div class="controls">
                        <g:textField name="title" class="span4"/>
                    </div>
                </div>

                <div class="control-group">
                    <label for="key">Key</label>

                    <div class="controls">
                        <g:textArea name="key" rows="5" cols="70" class="span4"/>
                    </div>
                </div>

                <div class="control-group">
                    <g:submitToRemote url="[mapping: 'account_ssh']"
                                      onSuccess="addSuccess()"
                                      onFailure="fieldError(XMLHttpRequest)"
                                      value="Add Public Key" class="btn btn-primary"/>
                </div>
            </fieldset>
        </g:formRemote>
    </div>
</div>

<r:script>
    function addSuccess(data) {
        clearErrors();
        $('#title').val('');
        $('#key').val('');
    }
    function fieldError(XMLHttpRequest) {
        if (XMLHttpRequest.status === 422) {
            clearErrors();
            var errors = eval("(" + XMLHttpRequest.responseText + ")").args;
            for (var i = 0; i < errors.length; i++) {
                $input = $('#' + errors[i].name);
                $input.parents('.control-group').addClass('error');
                $input.after($('<span class="help-inline">').text(errors[i].errorMessage));
            }
        }
    }
    function clearErrors() {
        $('.control-group').removeClass('error');
        $('.help-inline').remove();
    }
    $(function() {
        $.getJSON("${createLink(mapping: 'account_ssh')}", {format: 'json'}, function(keys) {
            for (var i = 0; i < keys.length; i++) {
                // create modal
                var $modal = $('<div>').addClass('modal');
                var $modal_body = $('<div>').addClass('modal-body').text('test').appendTo($modal);
                $modal.hide();
                $modal.appendTo($('#keys'));

                var $edit = $('<a>').text('(edit)').addClass('edit').click(function() {
                    $modal.modal('show');
                });

                // create box
                var $accordion_group = $('<div>').addClass('accordion-group');
                var $accordion_heading = $('<div>').addClass('accordion-heading').appendTo($accordion_group);
                var $title = $('<a>').attr('data-toggle', 'collapse')
                                                  .addClass('accordion-toggle')
                                                  .attr('data-parent', '#keys')
                                                  .attr('href', '#key_' + keys[i].id)
                                                  .append('<i class="icon-lock"></i>')
                                                  .append(keys[i].title)
                                                  .appendTo($accordion_heading);
                $accordion_heading.append($edit);
                var $accordion_body = $('<div>').attr('id', 'key_' + keys[i].id)
                                                .addClass('accordion-body collapse in')
                                                .appendTo($accordion_group);
                var $accordion_inner = $('<div>').addClass('accordion-inner')
                                                 .text(keys[i].key)
                                                 .appendTo($accordion_body);
                $(".collapse", $accordion_group).collapse();
                $accordion_group.appendTo($('#keys'));
            }
        });
    });
</r:script>
</body>
</html>