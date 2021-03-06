<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>
<%-- this file should contain solely the stylesheet, meta tags, and javascript elements needed in the head --%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<rs:resourceURL var="jQueryScript" value="/rs/jquery/1.4.2/jquery-1.4.2.min.js"/>
<script type="text/javascript" language="javascript" src="${jQueryScript}"></script>
<rs:resourceURL var="linkify" value="/js/linkify.js"/>
<script type="text/javascript" src="${linkify}"></script>

<rs:resourceURL var="mainStyle" value="/themes/jasig/css/main_one_column.css"/>
<link rel="stylesheet" href="${mainStyle}" type="text/css" media="all" />
<rs:resourceURL var="extras" value="/css/extras.css"/>
<link rel="stylesheet" href="${extras}" type="text/css" media="all" />