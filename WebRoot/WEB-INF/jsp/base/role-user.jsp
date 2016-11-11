<%@ page language="java" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
    var user_combogrid;
    $(function() {
        loadUser();
    });

    //加载用户
    function loadUser(){
        var isChange = false;
        user_combogrid = $('#userIds').combogrid({
            multiple:true,
            panelWidth:500,
            panelHeight:360,
            idField:'id',
            textField:'loginName',
            url:'${ctx}/base/user!combogridAll.action',
            mode: 'remote',
            fitColumns: true,
            striped: true,
            editable:false,
            rownumbers:true,//序号
            collapsible:false,//是否可折叠的
            fit: true,//自动大小
            method:'post',
            columns:[[
                {field:'ck',checkbox:true},
                {field:'id',title:'主键ID',width:100,hidden:'true'},
                {field:'loginName',title:'用户登录名',width:120,sortable:true},
                {field:'name',title:'用户姓名',width:80,sortable:true}
            ]],
            onBeforeLoad:function(param){
                param.filter_EQI_status = 0;//排除已删除的数据
            }
        });

    }
</script>
<div>
    <form id="role_user_form" method="post">
        <input type="hidden" id="id" name="id" />
        <!-- 用户版本控制字段 version -->
        <input type="hidden" id="version" name="version" />
        <div>
            <label>角色用户:</label>
            <input type="select" class="easyui-combogrid" id="userIds" name="userIds" style="width: 260px;"/>
        </div>
    </form>
</div>