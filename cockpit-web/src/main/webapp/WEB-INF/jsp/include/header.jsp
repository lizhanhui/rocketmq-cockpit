<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Static navbar -->
<nav class="navbar navbar-default">
    <div class="console-topbar-new">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">RocketMQ Cockpit</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Topic<span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="cockpit/topic/">Topics</a></li>
                            <li><a href="cockpit/topic-progress/">Topic Progress</a> </li>
                        </ul>
                    </li>

                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Consumer Group<span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="cockpit/consumer-group/">Consumer Groups</a></li>
                            <li><a href="cockpit/consume-progress/">Consumer Group Progress </a> </li>
                        </ul>
                    </li>
                <li><a href="cockpit/message/">Query Message</a></li>

                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Project<span class="caret"></span></a>
                    <ul class="dropdown-menu" role="menu">
                        <li><a href="cockpit/project/manage">Manage</a></li>
                        <li><a href="cockpit/project/create">Launch New Project</a> </li>
                    </ul>
                </li>

                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Admin<span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="cockpit/admin/user">Manage User</a></li>
                            <li><a href="cockpit/ip/">IP Mapping</a></li>
                            <li class="divider"></li>
                            <li><a href="cockpit/name-server/">Name Server List</a></li>
                            <li><a href="cockpit/name-server/kv">Name Server KV</a></li>
                            <li class="divider"></li>
                            <li><a href="console/">Console</a> </li>
                        </ul>
                    </li>
            </ul>

            <ul class="nav navbar-nav navbar-right">
                <li>
                    <a href="javascript:;">
                        <strong>
                            <span style="color: #5cb85c;">Hi</span>
                            <span style="color: #f0ad4e;">${handle_in_session}</span>
                        </strong>
                    </a>
                </li>
                <li><a href="cockpit/logout">Logout</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div><!--/.container-fluid -->
</nav>