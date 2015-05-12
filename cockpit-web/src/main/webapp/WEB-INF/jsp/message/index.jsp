<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value="Query Message" />
    </jsp:include>
  <script src="js/message.js" type="application/javascript"></script>
  <script src="js/onloading.js" type="application/javascript"></script>
</head>
<body>

    <jsp:include page="../include/header.jsp"></jsp:include>

<div class="container-fluid">
  <select id="queryType">
    <option value="0">Query Message By ID</option>
    <option value="1">Query Message By KEY</option>
  </select>

<br />
<br />
  <div class="clear-both"></div>
  <div id="queryID" style="display:block">

    <div class="col-xs-4 col-xs-offset-2">
      <input type="text" class="form-control msgId" placeholder="message id">
    </div>

    <div class="col-xs-2">
      <button type="submit" class="btn btn-primary queryByID">query</button>
    </div>

<br />
    <br />
    <div class="row">
      <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
        <table class="table table-condense table-hover table-bordered">
          <tbody class="table-striped itable-content">
          </tbody>
        </table>

        <div class="clear-both"></div>

      </div>
    </div>
  </div>

  <div id="queryKEY" style="display:none">

    <div class="col-xs-4 col-xs-offset-2">
      <input type="text" class="form-control msgTopic" placeholder="Message Topic">
    </div>

    <div class="col-xs-4">
      <input type="text" class="form-control msgKey" placeholder="message key">
    </div>

    <div class="col-xs-2">
      <button type="submit" class="btn btn-primary queryByKEY">query</button>
    </div>

<br />
    <br />
    <div class="row">
      <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
        <table class="table table-condense table-hover table-bordered">
          <thead>
          <tr>
            <td>Message ID</td>
            <td>Tag</td>
            <td>Key</td>
            <td>Storetime</td>
            <td>Operation</td>
          </tr>
          </thead>
          <tbody class="table-striped ktable-content">
          </tbody>
        </table>

        <div class="clear-both"></div>

      </div>
    </div>
  </div>

  <div id="flow" style="display:none">
    <div class="row">
      <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
        <table class="table table-condense table-hover table-bordered">
          <tbody class="table-striped ftable-content">
          </tbody>
        </table>

        <div class="clear-both"></div>

      </div>
    </div>
  </div>
</div>
</body>
</html>
