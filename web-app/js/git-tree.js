;
(function ($) {
    $.fn.gitTree = function (url, ref, options) {
        var opts = $.extend({}, $.fn.gitTree.defaults, options);

        return this.each(function () {
            var $this = $(this);
            var base = url;
            var path = opts.path;

            $this.append($('<div>this is path</div>'));

            var callback = function(data) {
                console.log(data);
            }

            getTreeFiles(url, ref, path, callback);
        });
    };

    function getTreeFiles(url, ref, path, callback) {
        $.ajax({
            type: "get",
            dataType: "json",
            url: url + ref + "/" + path,
            success: callback(data)
        });
    }

    $.fn.gitTree.defaults = {
        path:""
    }
})(jQuery);