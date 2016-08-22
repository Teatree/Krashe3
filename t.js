function getProjects() {

    Visualforce.remoting.Manager.invokeAction(
        '{!$RemoteAction.ReportingAppCtrl.getProjects}',
        function (result, event) {
            if (event.status) {
                for (var i = 0; i < options.length; i++) {
                    var opt = options[i];
                    var el = document.createElement("option");
                    el.textContent = opt;
                    el.value = opt;
                    select.appendChild(el);
                }
            } else if (event.type === 'exception') {
                document.getElementById("responseErrors").innerHTML =
                    event.message + "<br/>\n<pre>" + event.where + "</pre>";
            } else {
                document.getElementById("responseErrors").innerHTML = event.message;
            }
        },
        {escape: true}
    );
}