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

        <g:if test="${flash.sshEventMessage}">
            <div class="alert alert-success" data-alert="alert">
                <a class="close" data-dismiss="alert" href="#">&times;</a>
                ${flash.sshEventMessage}
            </div>
        </g:if>

        <div class="accordion" id="keys"></div>

        
        <a href="#" id="sshform-toggle">Add another public key</a>
        <g:formRemote id="sshform" name="sshForm" url="[mapping: 'account_ssh']"
                      onSuccess="addSuccess(data)"
                      onFailure="fieldError(XMLHttpRequest)">
            <fieldset>
                <div class="control-group">
                    <label for="title">Title</label>

                    <div class="controls">
                        <g:textField name="title" class="span5"/>
                    </div>
                </div>

                <div class="control-group">
                    <label for="key">Key</label>

                    <div class="controls">
                        <g:textArea name="key" rows="5" cols="70" class="span5"/>
                    </div>
                </div>

                <div class="control-group">
                    <g:submitToRemote url="[mapping: 'account_ssh']"
                                      onSuccess="location.reload();"
                                      onFailure="fieldError(XMLHttpRequest)"
                                      value="Add Public Key" class="btn btn-primary"/>
                    <span>or</span>
                    <a href="#" id="sshform-cancel">cancel</a>
                </div>
            </fieldset>
        </g:formRemote>
    </div>
</div>

<r:script>
    function fieldError(XMLHttpRequest) {
        if (XMLHttpRequest.status === 422) {
            clearErrors();
            var errors = eval("(" + XMLHttpRequest.responseText + ")").args;
            for (var i = 0; i < errors.length; i++) {
                var $input = $('#' + errors[i].name);
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
        $('#sshform').hide();
        $('#sshform-toggle').click(function(e){
            e.preventDefault();
            $(this).hide();
            $('#sshform').show();
        });
        $('#sshform-cancel').click(function(e){
            e.preventDefault();
            $('#sshform-toggle').show();
            $('#sshform').hide();
        });
        $.getJSON("${createLink(mapping: 'account_ssh')}", {format: 'json'}, function(keys) {
            for (var i = 0; i < keys.length; i++) {
                (function(key) {
                    // create modal
                    var $modal = createEditModal(key);
                    var $edit = $('<a>').text('(edit)').addClass('edit').click(function() {
                        $modal.modal('show');
                    });

                    // create box
                    var $accordion_group = $('<div>').addClass('accordion-group');
                    var $accordion_heading = $('<div>').addClass('accordion-heading').appendTo($accordion_group);
                    var $title = $('<a>').attr('data-toggle', 'collapse')
                                                  .addClass('accordion-toggle')
                                                  .attr('data-parent', '#keys')
                                                  .attr('href', '#key_' + key.id)
                                                  .append('<i class="icon-lock"></i>')
                                                  .append(key.title)
                                                  .appendTo($accordion_heading);
                    $accordion_heading.append($edit);
                    $accordion_heading.append($('<a>').attr('href', key.url).append('<i class="icon-remove pull-right">').click(function(e){
                        e.preventDefault();
                        if(window.confirm('Are you sure you want to delete this key?')){
                            $.ajax({
                                type:'POST',
                                data: "_method=delete",
                                url:$(this).attr('href'),
                                success:function(data,textStatus){
                                    location.reload();
                                }
                            });
                        }
                    }));
                    var $accordion_body = $('<div>').attr('id', 'key_' + key.id)
                                                .addClass('accordion-body collapse in')
                                                .appendTo($accordion_group);
                    var $accordion_inner = $('<div>').addClass('accordion-inner')
                                                 .text(key.key)
                                                 .appendTo($accordion_body);
                    $(".collapse", $accordion_group).collapse();
                    $accordion_group.appendTo($('#keys'));
                })(keys[i]);
            }
        });
    });
    function createEditModal(key) {
        var $modal = $('<div>').addClass('modal ssh-edit');

        var $modal_header = $('<div class="modal-header"><a class="close" data-dismiss="modal" href="#">×</a><h3>Edit Your Key</h3></div>').appendTo($modal);
        var $modal_body = $('<div>').addClass('modal-body').appendTo($modal);

        var $form = $('<form method="post">').attr('action', key.url).appendTo($modal_body);
        $form.append('<input type="hidden" name="_method" value="put">');
        var $fieldSet = $('<fieldset>').appendTo($form);

        var $titleControlGroup = $('<div class="control-group">').appendTo($fieldSet);

        var $titleLabel = $('<label for="title">Title</label>').appendTo($titleControlGroup);
        var $titleInput= $('<div class="controls">').append($('<input name="title" class="span5">').val(key.title))
                                                    .appendTo($titleControlGroup);

        var $keyControlGroup = $('<div class="control-group">').appendTo($fieldSet);
        var $keyLabel = $('<label for="key">Key</label>').appendTo($keyControlGroup);
        var $KeyInput= $('<div class="controls">').append($('<textarea name="key" rows="5" cols="70" class="span5">').val(key.key))
                                                  .appendTo($keyControlGroup);

        $form.submit(function(e) {
            $.ajax({
                type:'POST',
                data:jQuery(this).serialize(),
                url:key.url,
                beforeSend:function(jqXHR, settings) {
                    $titleControlGroup.removeClass('error');
                    $('.help-inline', $titleInput).remove();
                    $keyControlGroup.removeClass('error');
                    $('.help-inline', $KeyInput).remove();
                },
                success:function(data,textStatus){
                    location.reload();
                },
                error:function(XMLHttpRequest,textStatus,errorThrown){
                    if (XMLHttpRequest.status === 422) {
                        var errors = eval("(" + XMLHttpRequest.responseText + ")");
                        for (var i = 0; i < errors.length; i++) {
                            var error = errors[i];
                            if (error.name === 'title') {
                                $titleControlGroup.addClass('error');
                                $titleInput.append($('<span class="help-inline">').text(error.errorMessage));
                            } else if (error.name === 'key') {
                                $keyControlGroup.addClass('error');
                                $KeyInput.append($('<span class="help-inline">').text(error.errorMessage));
                            }
                        }
                    }
                }
            });
            return false;
        });

        var $modal_footer = $('<div class="modal-footer">').appendTo($modal);
        var $update_link = $('<a class="btn btn-primary" href="#">Save Changes</a>').click(function(e) {
            e.preventDefault();
            $form.submit();
        }).appendTo($modal_footer);
        $modal.hide();
        $modal.appendTo($('#keys'));
        return $modal;
    };
</r:script>
</body>
</html>