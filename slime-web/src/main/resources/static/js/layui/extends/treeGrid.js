/**
 @Name：treeGridTree Table
 @Author：beijiyi
 @version: 0.1
 Code Cloud Address：https://gitee.com/beijiyi/tree_table_treegrid_based_on_layui
 Offlinedemo：http://beijiyi.com/
 */
layui.define(['laytpl', 'laypage', 'layer', 'form'], function(exports){
    "use strict";
    var $ = layui.$
        ,laytpl = layui.laytpl
        ,laypage = layui.laypage
        ,layer = layui.layer
        ,form = layui.form
        ,hint = layui.hint()
        ,device = layui.device()
        //External Interface
        ,table = {
            config: {//Global Configuration Item,Form Level
                indexName: 'lay_table_index' //Index name underline
                ,cols:{//Additional field of the node type
                    isCheckName: 'lay_is_checked' //Select Status（true，false）
                    ,isRadio:'lay_is_radio'//Please translate the following text to english（true，false）
                    ,isOpen:'lay_is_open'//Whether to expand the child's node
                    ,isShow:'lay_is_show'//Whether to show the balance
                    ,level:'lay_level'//Relationships（不需要1 hour before appointment）
                    ,children:'children'//Save Assistant Variable

                    ,cheDisabled:'lay_che_disabled'//Select None（true，false）
                    ,radDisabled:'lay_rad_disabled'//Select None（true，false）

                    ,iconOpen:'lay_icon_open'//Open Icon
                    ,iconClose:'lay_icon_close'//The icon of the command to be run.
                    ,icon:'lay_icon'//Leaf node icon
                }
                ,initWidth:{//Default column width definition
                    checkbox: 48
                    ,space: 15
                    ,numbers: 40
                    ,radio:48
                }
            }
            /**
             * Data Cache
             *
             * Structure Diagram
             * cache{}                  Cache（对象）
             *      key['data']{}       All Data Cache（对象）
             *          key[list][]     List Data Object（The following text is a sample answer to the question "What is your name?":My name is John Doe.）
             *          key[map]{}      List DataMap对象（Map）
             *          key[treeList][] An object with a tree structure（The following text is a sample answer to the question "What is your name?":My name is John Doe.）
             *      key['cla']{}        All pre-initializedCalssClass Object（Note that this is a non-blocking operation）
             *          key['claIds'][] Form classes that have been initialized with the assistant
             *          key[claObjs]{key[tableId]}  All ready initializedcla对象
             *
             */
            ,cache: {
                tableId:{
                    data:{
                        list:[]//List Data
                        ,map:{}//List data inidFieldor only valuekey的MapData
                        ,treeList:[]//Tree Data
                        ,upIds:[]//Collecting data  Strictly follow the order of the first call to an external service
                    }
                }
                ,cla:{
                    claIds:{
                        tableId:true
                    }
                    ,claObjs:{
                        tableId:{}
                    }
                }
                ,selectcode:{//Data dictionary cache
                    demokey:[
                        {
                            key:{value:''}
                        }
                    ]
                }
            }
            ,index: layui.table ? (layui.table.index + 10000) : 0
            /**
             * Set Global Variable
             * @param options
             * @return {table}
             */
            ,set: function(options){
                var that = this;
                that.config = $.extend({}, that.config, options);
                return that;
            }
            /**
             * Translate the following text to english: Event Monitoring
             * @param events
             * @param callback
             * @return {*}
             */
            ,on: function(events, callback){
                return layui.onevent.call(this, MOD_NAME, events, callback);
            }
            ,getClass:function (tableId) {
                return table.cache.cla.claObjs[tableId];;
            }
            ,pushClass:function (tableId,that) {
                table.cache.cla.claObjs[tableId]=that;
            }
            ,isCalss:function (tableId) {
                var ids=this.cache.cla.claIds||{};
                return  ids.hasOwnProperty(tableId)||false;
            }
            ,isClassYes:function (tableId) {
                var ids=this.cache.cla.claIds||{};
                return ids[tableId]||false;
            }
            ,pushClassIds:function (tableId,is) {
                this.cache.cla.claIds[tableId]=is;
            }
            ,setObj:function (tableId,key,o) {
                if(!this.obj[tableId])this.obj[tableId]={};
                this.obj[tableId][key]=o;
            }
            ,getObj:function (tableId, key) {
                return this.obj[tableId]?this.obj[tableId][key]:null;
            }
            /**
             * Get List Data
             */
            ,getDataList:function (tableId) {
                if(table.cache[tableId]){
                    return table.cache[tableId].data.list;
                }
                return [];
            }
            /**
             * Set List Data
             */
            ,setDataList:function (tableId, list) {
                if(!table.cache[tableId])table.cache[tableId]={};
                if(!table.cache[tableId].data)table.cache[tableId].data={};
                if(!table.cache[tableId].data.list)table.cache[tableId].data.list=[];
                table.cache[tableId].data.list=list;
            }
            /**
             * Get List Data
             */
            ,getDataMap:function (tableId) {
                if(table.cache[tableId]){
                    return table.cache[tableId].data.map;
                }
                return {};
            }
            /**
             * Set List Data
             */
            ,setDataMap:function (tableId, map) {
                if(!table.cache[tableId])table.cache[tableId]={};
                if(!table.cache[tableId].data)table.cache[tableId].data={};
                if(!table.cache[tableId].data.map)table.cache[tableId].data.map={};
                table.cache[tableId].data.map=map;
            }
            /**
             * Get Tree Data
             */
            ,getDataTreeList:function (tableId) {
                if(table.cache[tableId]){
                    return table.cache[tableId].data.treeList;
                }
                return [];
            }
            /**
             * Set Up Tree Data
             */
            ,setDataTreeList:function (tableId, treeList) {
                if(!table.cache[tableId])table.cache[tableId]={};
                if(!table.cache[tableId].data)table.cache[tableId].data={};
                if(!table.cache[tableId].data.treeList)table.cache[tableId].data.treeList={};
                table.cache[tableId].data.treeList=treeList;
            }
            /**
             * Get root node data
             */
            ,getDataRootList:function (tableId) {
                if(table.cache[tableId]){
                    return table.cache[tableId].data.upIds||[];
                }
                return [];
            }
            /**
             * Please set the root node data
             */
            ,setDataRootList:function (tableId, rootList) {
                if(!table.cache[tableId])table.cache[tableId]={};
                if(!table.cache[tableId].data)table.cache[tableId].data={};
                if(!table.cache[tableId].data.upIds)table.cache[tableId].data.upIds=[];
                table.cache[tableId].data.upIds=rootList;
            }
            /**
             * Initializing
             * @param filter
             * @param settings
             * @return {table}
             */
            ,init:function (filter, settings) {
                settings = settings || {};
                var that = this
                    ,elemTable = filter ? $('table[lay-filter="'+ filter +'"]') : $(ELEM + '[lay-data]')
                    ,errorTips = 'Table element property lay-data configuration item has a syntax error: ';
                //Number of spaces:
                elemTable.each(function(){
                    var othis = $(this), tableData = othis.attr('lay-data');
                    try{
                        tableData = new Function('return '+ tableData)();
                    } catch(e){
                        hint.error(errorTips + tableData)
                    }
                    var cols = [], options = $.extend({
                        elem: this
                        ,cols: []
                        ,data: []
                        ,skin: othis.attr('lay-skin') //Personal Data
                        ,size: othis.attr('lay-size') //Size
                        ,even: typeof othis.attr('lay-even') === 'string' //Even Row Background
                    }, table.config, settings, tableData);

                    filter && othis.hide();

                    //Getting table header data
                    othis.find('thead>tr').each(function(i){
                        options.cols[i] = [];
                        $(this).children().each(function(ii){
                            var th = $(this), itemData = th.attr('lay-data');

                            try{
                                itemData = new Function('return '+ itemData)();
                            } catch(e){
                                return hint.error(errorTips + itemData)
                            }

                            var row = $.extend({
                                title: th.text()
                                ,colspan: th.attr('colspan') || 0 //List Column
                                ,rowspan: th.attr('rowspan') || 0 //Row replacement
                            }, itemData);

                            if(row.colspan < 2) cols.push(row);
                            options.cols[i].push(row);
                        });
                    });

                    //Getting form data
                    othis.find('tbody>tr').each(function(i1){
                        var tr = $(this), row = {};
                        //If field name is defined
                        tr.children('td').each(function(i2, item2){
                            var td = $(this)
                                ,field = td.data('field');
                            if(field){
                                return row[field] = td.html();
                            }
                        });
                        //If you don't know the answer to a question, please don't share false information.
                        layui.each(cols, function(i3, item3){
                            var td = tr.children('td').eq(i3);
                            row[item3.field] = td.html();
                        });
                        options.data[i1] = row;
                    });
                    table.render(options);
                });

                return that;
            }
            /**
             * Enter passphrase for key %1:（At the heart of the entrance）
             */
            ,render:function (options) {
                table.pushClassIds(options.id);
                var inst = new Class(options);
                return thisTable.call(inst);
            }
            /**
             * Form action completed successfully(Method is unimplemented，Please useparseData代替)
             * @param tableId
             * @param fn
             */
            ,ready:function (tableId,fn) {
                var is=false;
                var myDate=new Date();
                function isReady() {
                    if(tableId){
                        var that=table.getClass(tableId);
                        if(that&&that.hasOwnProperty('layBody')){
                            fn(that);
                            is=true;
                        }else{
                            var myDate2=new Date();
                            var i=myDate2.getTime()-myDate.getTime();
                            if(i<=(1000*10)&&!is){//>10Seconds
                                setTimeout(isReady,50);
                            }
                        }
                    }
                }
                if(tableId&&fn){
                    setTimeout(isReady,50);
                }
            }
            /**
             * Get the selected records from a table
             * @param tableId
             * @return {{data: Array, isAll: boolean}}
             */
            ,checkStatus:function (tableId) {
                var nums = 0
                    ,invalidNum = 0
                    ,arr = []
                    ,data = table.getDataList(tableId) || [];
                //Calculate the number of combinations
                layui.each(data, function(i, item){
                    if(item.constructor === Array){
                        invalidNum++; //Invalid data，or removed
                        return;
                    }
                    if(item[table.config.cols.isCheckName]){
                        nums++;
                        arr.push(table.clearCacheKey(item));
                    }
                });
                return {
                    data: arr //Selected Data
                    ,isAll: data.length ? (nums === (data.length - invalidNum)) : false //Are you sure you want to select all?
                };
            }
            /**
             * Set form action to true
             * @param tableId
             * @param value     Set this value to true to disable showing the restart buttons in the login window.
             * @returns {*}
             */
            ,setCheckStatus:function(tableId, fildName, ids){
                var retObj=null;
                var that=table.getClass(tableId)
                    ,invalidNum = 0
                    ,arr = []
                    ,data = table.getDataList(tableId) || []
                    ,childs = that.layBody.find('input[name="'+TABLE_CHECKBOX_ID+'"]')//Check Box
                ;
                if(fildName&&ids){//Set Selected
                    var idsarr=ids.split(',');
                    idsarr.forEach(function (o) {
                        var temo=null;
                        data.forEach(function (e) {
                            var b1=e[fildName]+"";
                            var b2=o+"";
                            if(b1==b2){
                                temo=e;
                                return;
                            };
                        });
                        if(temo){
                            var v=temo[table.config.indexName];
                            that.layBody.find('input[name="'+TABLE_CHECKBOX_ID+'"][value="'+v+'"]').prop("checked",true);
                            that.setCheckData(v, true);
                        }
                    });
                    that.syncCheckAll();
                    that.renderForm('checkbox');
                }
                return retObj;
            }
            /**
             * Form action status
             * @param tableId
             * @param value     Set this value to true to disable showing the restart buttons in the login window.
             * @returns {*}
             */
            ,radioStatus:function (tableId) {
                var that=table.getClass(tableId);
                var retObj=null;
                var nums = 0
                    ,invalidNum = 0
                    ,arr = []
                    ,data = table.getDataList(tableId) || [];
                var v=that.layBody.find("input[name='"+TABLE_RADIO_ID+"']:checked").val();
                v=parseInt(v);
                data.forEach(function (e) {
                    if(e[table.config.indexName]==v){
                        retObj=e;
                    };
                });
                return table.clearCacheKey(retObj);
            }
            /**
             * Set form field to single selection
             * @param tableId
             * @param value     Set this value to true to disable showing the restart buttons in the login window.
             * @returns {*}
             */
            ,setRadioStatus:function (tableId,fildName,value) {
                var that=table.getClass(tableId);
                var retObj=null;
                var nums = 0
                    ,invalidNum = 0
                    ,arr = []
                    ,data = table.getDataList(tableId) || [];

                if(fildName&&value){//Set Selected
                    data.forEach(function (e) {
                        var b1=e[fildName]+"";
                        var b2=value+"";
                        if(b1==b2){
                            retObj=e;
                            return;
                        };
                    });

                    if(retObj){
                        var v=retObj[table.config.indexName];
                        that.layBody.find("input:radio[name='"+TABLE_RADIO_ID+"'][value='"+v+"']").prop("checked",true);
                        form.render('radio');
                    }
                }
                return retObj;
            }
            /**
             * Clear ResetKey
             * @param data
             * @return {*}
             */
            ,clearCacheKey:function (data) {
                data = $.extend({}, data);
                delete data[table.config.cols.isCheckName];
                delete data[table.config.indexName];
                return data;
            }
            /**
             *  Refresh Data
             * @param id
             * @param options
             * @return {*}
             */
            ,query:function (tableId, options) {
                var that= table.getClass(tableId);
                that.renderTdCss();
                if(that.config.data && that.config.data.constructor === Array) delete that.config.data;
                that.config = $.extend({}, that.config, options);
                that.pullData(that.page, that.loading());
            }
            /**
             * This method re-renders the whole（Rating:）
             * @param id
             * @param options
             */
            ,reload:function (tableId, options) {
                var config = thisTable.config[tableId];
                options = options || {};
                if(!config) return hint.error('The ID option was not found in the table instance');
                if(options.data && options.data.constructor === Array) delete config.data;
                return table.render($.extend(true, {}, config, options));
            }
            /**
             * Add one or more rows of data
             * @param tableId   Formid
             * @param index     Inserting at %s（From0Start）
             * @param data      Data
             * @returns {*}
             */
            ,addRow:function (tableId, index, data) {
                var that=table.getClass(tableId)
                    ,options=that.config
                    ,uo = []//Parent node
                    ,treeList=table.getDataTreeList(tableId)
                    ,list = table.getDataList(tableId) || [];
                that.resetData(data);
                //Insert after the father node
                list.splice(index,0,data);//Updated cache
                table.kit.restNumbers(list);//Reset Subscription
                table.setDataMap(tableId,that.resetDataMap(list));//Answermap
                if(options.isTree){//Processing Level
                    //1、Process parent  2、AnswertreeObj 3、Level
                    var uo=that.treeFindUpData(data);
                    if(uo) {
                        var clist=uo.children;
                        uo.children.push(data);
                        data[table.config.cols.level]=uo[table.config.cols.level]+1;
                    }else{
                        data[table.config.cols.level]=1;
                        treeList.push(data);
                    }
                }
                //Generatinghtml
                var tds=that.renderTr(data,data[table.config.indexName]);
                var trs='<tr data-index="'+ data[table.config.indexName] +'"'+that.renderTrUpids(data)+'>'+ tds.join('') + '</tr>';
                if(index==0){//Insert at first position
                    var  tbody=that.layBody.find('table tbody');
                    $(tbody).prepend(trs);
                    that.layBody.find(".layui-none").remove();
                }else{
                    var o=that.layBody.find('[data-index='+(index-1)+']');//The following sources are available:domTree
                    $(o).after(trs);
                }
                that.renderForm();
                if(options.isPage)that.renderPage(that.config.page.count+1);//Page Handling
                that.restNumbers();
                that.events();
                if(options.isTree) {//Expand node
                    that.treeNodeOpen(uo, true);
                    that.renderTreeConvertShowName(uo);
                }
            }
            /**
             * Delete one or more rows of data
             * （If true, expand the child nodes in the tree view）
             * @param tableId
             * @param data（1、The following text is a sample answer to the question "What is your name?":My name is John Doe.；2、对象）
             */
            ,delRow:function (tableId, data) {
                //1、Clear the page 2、Cache Clear
                var that=table.getClass(tableId)
                    ,options=that.config
                    ,list=table.getDataList(tableId);
                var sonList=[];//Data to Remove
                var delIds={};//Data to Removemap
                var delDatas=[];
                var upDelDatas=[];//Delete all the selected nodes and their children（Collapse All Groups）
                if(!that||!data)return;
                if(table.kit.isArray(data)){//Is an array，Remove Multiple
                    delDatas=data;
                }else{
                    delDatas[0]=data;
                }
                delDatas.forEach(function(temo) {//Record all parent nodes
                    var uo=that.treeFindUpData(temo);
                    if(uo){
                        upDelDatas.push(uo);
                    }
                });
                sonList=options.isTree?table.treeFindSonList(that.config.id,delDatas):delDatas;
                sonList.forEach(function (temo) {//Translate the following text to english
                    var index=temo[table.config.indexName];
                    delIds[index]=index;//Set ReplacementidConversation
                    var tr = that.layBody.find('tr[data-index="'+ index +'"]');
                    tr.remove();
                });
                that.restNumbers();//Data Processing
                var newList=[];//Construct a new array
                for (var i=0,len=list.length;i<len;i++) {
                    var isP=true;
                    var temo1=null;
                    sonList.forEach(function (temo) {
                        if (temo[table.config.indexName] === list[i][table.config.indexName]) {
                            isP = false;
                        }
                    });
                    if(isP){
                        newList.push(list[i]);
                    }
                }
                table.kit.restNumbers(newList);//Rename the selection
                table.setDataList(tableId,newList);//Answerlist
                table.setDataMap(tableId,that.resetDataMap(newList));//Answermap
                table.setDataTreeList(tableId,that.resetDataTreeList(newList,table.getDataRootList(tableId)));//Collapse All Groups
                upDelDatas.forEach(function(temo) {//Parent node handling
                    that.renderTreeConvertShowName(temo);
                });
                if(options.isPage)that.renderPage(that.config.page.count-Object.keys(delIds).length);//Page Handling
                that.events();//Re-enrollment
            }
            /**
             * Update the selected record
             * @param obj
             * @param index
             */
            ,updateRow:function (tableId,obj) {
                var that=table.getClass(tableId);
                if(!that||!obj)return;
                var id=obj[that.config.idField];
                //Updated cache data
                var maps=table.getDataMap(tableId);
                var thisobj=maps[id];
                if(thisobj){
                    $.extend(thisobj, obj);
                }else{
                    return;
                }
                //Update Page
                var oi=thisobj[table.config.indexName];
                var  tds=that.renderTr(thisobj,oi);
                var tr=that.layBody.find('tr[data-index='+oi+']');
                $(tr).html(tds);
            }
            ,treeNodeOpen:function (tableId,o, isOpen) {
                var that=table.getClass(tableId);
                if(!that||!o)return;
                that.treeNodeOpen(o,isOpen);
            }
            /**
             * Collapse or expand all(Default to Expanded)
             * @param tableId
             * @param isOpen    Expand or Collapse（Default Value Settrue Expand）
             */
            ,treeOpenAll:function (tableId,isOpen) {
                var that=table.getClass(tableId);
                if(!that)return;
                if(isOpen===undefined){isOpen=true;}
                var list=table.getDataList(tableId);
                if(!list)return;
                list.forEach(function (temo) {
                    that.treeNodeOpen(temo,isOpen);
                });
            }
            /**
             * Get all required subnode objects
             * @param data（String or object）
             */
            ,treeFindSonList:function (tableId,data) {
                var that=table.getClass(tableId);
                if(!that||!data)return [];
                var delDatas=[];
                var sonList=[];//Data to Remove
                var delIds={};//Data to Removemap
                if(table.kit.isArray(data)){//Is an array，Remove Multiple
                    delDatas=data;
                }else{
                    delDatas[0]=data;
                }
                delDatas.forEach(function (temo) {
                    if(temo.children.length>0){
                        var temSonList=that.treeFindSonData(temo);
                        temSonList.forEach(function (temii) {
                            if(!delIds[temii[table.config.indexName]]){
                                sonList.push(temii);
                                delIds[temii[table.config.indexName]]=temii[table.config.indexName];
                            }
                        });
                    }
                    sonList.push(temo);
                    delIds[temo[table.config.indexName]]=temo[table.config.indexName];
                });
                return sonList;
            }
            ,treeFindUpDatas:function (tableId, o) {
                var that=table.getClass(tableId);
                if(!that||!o)return [];
                return that.treeFindUpDatas(o);
            }
            ,treeFindUpData:function (tableId, o) {
                var that=table.getClass(tableId);
                if(!that||!o)return [];
                return that.treeFindUpData(o);
            }
            /**
             * Get all the required subnodesidConversation
             * @param data（String or object）
             */
            ,treeFindSonIds:function (tableId,data) {
                var delIds=[];
                var sonList=table.treeFindSonList(tableId,data);
                sonList.forEach(function (temo) {
                    delIds.push([table.config.indexName]);
                });
                return delIds;
            }
            /**
             * Get all theidQuestion Set
             * @param tableId
             * @param data
             * @returns {Array}
             */
            ,treeFindSonIdFields:function (tableId,data) {
                var idField=[];
                var that=table.getClass(tableId);
                var sonList=table.treeFindSonList(tableId,data);
                sonList.forEach(function (temo) {
                    idField.push(temo[that.config.idField]);
                });
                return idField;
            }
            ,treeIconRender:function (tableId, o) {
                var that=table.getClass(tableId);
                if(!that||!o)return [];
                return that.treeIconRender(o,false);
            }
            /**
             * Translation Methods Object
             */
            ,kit:{
                isArray:function (o) {
                    return Object.prototype.toString.call(o) === '[object Array]';
                }
                ,isNumber:function (val){
                    var regPos = /^\d+(\.\d+)?$/; //Non-negative float
                    var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //Point Number
                    if(regPos.test(val) || regNeg.test(val)){
                        return true;
                    }else{
                        return false;
                    }

                }
                ,restNumbers:function (list) {
                    if(!list)return;
                    var i=0;
                    list.forEach(function (o) {
                        o[table.config.indexName]=i;
                        i++;
                    });
                }
            }
        }
        //Operation on current instance
        ,thisTable = function(){
            var that = this
                ,options = that.config
                ,id = options.id;
            id && (thisTable.config[id] = options);
            return {
                reload: function(options){
                    that.reload.call(that, options);
                }
                ,config: options
            }
        }
        //Character Constants
        ,MOD_NAME = 'treeGrid', ELEM = '.layui-table', THIS = 'layui-this', SHOW = 'layui-show', HIDE = 'layui-hide', DISABLED = 'layui-disabled', NONE = 'layui-none'
        ,ELEM_VIEW = 'layui-table-view', ELEM_HEADER = '.layui-table-header', ELEM_BODY = '.layui-table-body', ELEM_MAIN = '.layui-table-main', ELEM_FIXED = '.layui-table-fixed', ELEM_FIXL = '.layui-table-fixed-l', ELEM_FIXR = '.layui-table-fixed-r', ELEM_TOOL = '.layui-table-tool', ELEM_PAGE = '.layui-table-page', ELEM_SORT = '.layui-table-sort', ELEM_EDIT = 'layui-table-edit', ELEM_HOVER = 'layui-table-hover'
        ,TABLE_RADIO_ID='table_radio_',TABLE_CHECKBOX_ID='layTableCheckbox'
        ,ELEM_FILTER='.layui-table-filter'
        ,TREE_ID='treeId',TREE_UPID='treeUpId',TREE_SHOW_NAME='treeShowName',TREE_KEY_MAP='tree_key_map'
        //theadRegion Templates
        ,TPL_HEADER = function(options){
            var rowCols = '{{#if(item2.colspan){}} colspan="{{item2.colspan}}"{{#} if(item2.rowspan){}} rowspan="{{item2.rowspan}}"{{#}}}';
            options = options || {};
            return ['<table cellspacing="0" cellpadding="0" border="0" class="layui-table" '
                ,'{{# if(d.data.skin){ }}lay-skin="{{d.data.skin}}"{{# } }} {{# if(d.data.size){ }}lay-size="{{d.data.size}}"{{# } }} {{# if(d.data.even){ }}lay-even{{# } }}>'
                ,'<thead>'
                ,'{{# layui.each(d.data.cols, function(i1, item1){ }}'
                ,'<tr>'
                ,'{{# layui.each(item1, function(i2, item2){ }}'
                ,'{{# if(item2.fixed && item2.fixed !== "right"){ left = true; } }}'
                ,'{{# if(item2.fixed === "right"){ right = true; } }}'
                ,function(){
                    if(options.fixed && options.fixed !== 'right'){
                        return '{{# if(item2.fixed && item2.fixed !== "right"){ }}';
                    }
                    if(options.fixed === 'right'){
                        return '{{# if(item2.fixed === "right"){ }}';
                    }
                    return '';
                }()
                ,'<th data-field="{{ item2.field||i2 }}" {{# if(item2.minWidth){ }}data-minwidth="{{item2.minWidth}}"{{# } }} '+ rowCols +' {{# if(item2.unresize){ }}data-unresize="true"{{# } }}>'
                ,'<div class="layui-table-cell laytable-cell-'
                ,'{{# if(item2.colspan > 1){ }}'
                ,'group'
                ,'{{# } else { }}'
                ,'{{d.index}}-{{item2.field || i2}}'
                ,'{{# if(item2.type !== "normal"){ }}'
                ,' laytable-cell-{{ item2.type }}'
                ,'{{# } }}'
                ,'{{# } }}'
                ,'" {{#if(item2.align){}}align="{{item2.align}}"{{#}}}>'
                ,'{{# if(item2.type === "checkbox"){ }}' //Check Box
                ,'<input type="checkbox" name="layTableCheckbox" lay-skin="primary" lay-filter="layTableAllChoose">'
                ,'{{# } else { }}'
                ,'<span>{{item2.title||""}}</span>'
                ,'{{# if(!(item2.colspan > 1) && item2.sort){ }}'
                ,'<span class="layui-table-sort layui-inline"><i class="layui-edge layui-table-sort-asc"></i><i class="layui-edge layui-table-sort-desc"></i></span>'
                ,'{{# } }}'
                ,'{{# } }}'
                ,'</div>'
                ,'</th>'
                ,(options.fixed ? '{{# }; }}' : '')
                ,'{{# }); }}'
                ,'</tr>'
                ,'{{# }); }}'
                ,'</thead>'
                ,'</table>'].join('');
        }
        /**
         * Incoming chat request
         */
        ,TPL_FILTER = function(options){
        }
        //tbodyRegion Templates
        ,TPL_BODY = ['<table cellspacing="0" cellpadding="0" border="0" class="layui-table" '
            ,'{{# if(d.data.skin){ }}lay-skin="{{d.data.skin}}"{{# } }} {{# if(d.data.size){ }}lay-size="{{d.data.size}}"{{# } }} {{# if(d.data.even){ }}lay-even{{# } }}>'
            ,'<tbody></tbody>'
            ,'</table>'].join('')
        //The main template
        ,TPL_MAIN = ['<div class="layui-form layui-border-box {{d.VIEW_CLASS}}" lay-filter="LAY-table-{{d.index}}" style="{{# if(d.data.width){ }}width:{{d.data.width}}px;{{# } }} {{# if(d.data.height){ }}height:{{d.data.height}}px;{{# } }}">'

            ,'{{# if(d.data.toolbar){ }}'
            ,'<div class="layui-table-tool"></div>'
            ,'{{# } }}'

            ,'<div class="layui-table-box">'
            ,'{{# var left, right; }}'
            ,'<div class="layui-table-header">'
            ,TPL_HEADER()
            ,'</div>'
            ,'<div class="layui-table-filter">'
            ,TPL_FILTER()
            ,'</div>'
            ,'<div class="layui-table-body layui-table-main">'
            ,TPL_BODY
            ,'</div>'

            ,'{{# if(left){ }}'
            ,'<div class="layui-table-fixed layui-table-fixed-l">'
            ,'<div class="layui-table-header">'
            ,TPL_HEADER({fixed: true})
            ,'</div>'
            ,'<div class="layui-table-body">'
            ,TPL_BODY
            ,'</div>'
            ,'</div>'
            ,'{{# }; }}'

            ,'{{# if(right){ }}'
            ,'<div class="layui-table-fixed layui-table-fixed-r">'
            ,'<div class="layui-table-header">'
            ,TPL_HEADER({fixed: 'right'})
            ,'<div class="layui-table-mend"></div>'
            ,'</div>'
            ,'<div class="layui-table-body">'
            ,TPL_BODY
            ,'</div>'
            ,'</div>'
            ,'{{# }; }}'
            ,'</div>'

            ,'{{# if(d.data.isPage){ }}'
            ,'<div class="layui-table-page">'
            ,'<div id="layui-table-page{{d.index}}"></div>'
            ,'</div>'
            ,'{{# } }}'

            /*,'<style>'
            ,'{{# layui.each(d.data.cols, function(i1, item1){'
            ,'layui.each(item1, function(i2, item2){ }}'
            ,'.laytable-cell-{{d.index}}-{{item2.field||i2}}{ '
            ,'{{# if(item2.width){ }}'
            ,'width: {{item2.width}}px;'
            ,'{{# } }}'
            ,' }'
            ,'{{# });'
            ,'}); }}'
            ,'</style>'*/
            ,'</div>'].join('')
        ,_WIN = $(window)
        ,_DOC = $(document)

        //构造器
        ,Class = function(options){
            var that = this;
            that.index = ++table.index;
            that.config = $.extend({}, that.config, table.config, options);
            that.configFirst = $.extend({}, that.config, table.config, options);
            that.render();
            table.pushClass(options.id,that);
        };
    /**
     * Form action properties（Default Configuration）
     */
    Class.prototype.config = {
        limit: 10 //Show per page
        ,loading: true //When requesting data，Whether to showloading
        ,cellMinWidth: 60 //All cells default to a minimum width
        ,heightRemove:[]//In the following situations，Elevation to be excluded from the calculation
        ,text: {
            none: 'Data Engine'
        }
        ,isFilter:false//Whether to enable internal filters
        ,method:'post'//Default Output DevicepostMode Request Backend
        ,radDisabledNum:0//Record & count:
        ,cheDisabledNum:0//Record & count:
        //Tree related diagrams
        ,branch: ['&#xe622;', '&#xe624;'] //The following sources are available:
        ,leaf: '&#xe621;' //叶节点
        ,iconOpen:true//Default to On
        ,isOpenDefault:true//Default to Expanded or Collapsed State
        ,parseData:null//Loading data from the server...
        ,onClickRow:null//row
        ,onDblClickRow:null//row double click event
        ,onBeforeCheck:null//Select Time
        ,onCheck:null//Check Events  (obj 对象,checked Select Status,isAll Are you sure you want to select all?)
        ,onRadio:null//Select Date  （）
        ,isTree:true//Default to Tree Table
        ,isPage:false//Page %1
        ,height:'100%'//Default height100%
    };
    Class.prototype.configFirst={};//Define Custom Slide Show
    //Form input
    Class.prototype.render = function(){
        var that = this
            ,options = that.config;
        options.elem = $(options.elem);
        options.where = options.where || {};
        options.id = options.id || options.elem.attr('id');
        that.test();
        //Custom format of the clock face
        options.request = $.extend({
            pageName: 'page'
            ,limitName: 'limit'
        }, options.request)
        //Respond to data
        options.response = $.extend({
            statusName: 'code'
            ,statusCode: 0
            ,msgName: 'msg'
            ,dataName: 'data'
            ,countName: 'count'
        }, options.response);
        //If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information. page Import Data laypage 对象
        if(typeof options.page === 'object'){
            options.limit = options.page.limit || options.limit;
            options.limits = options.page.limits || options.limits;
            that.page = options.page.curr = options.page.curr || 1;
            delete options.page.elem;
            delete options.page.jump;
        }
        if(!options.elem[0]) return that;
        that.columnWidthInit();//Width Calculator
        //Start inserting alternative elements
        var othis = options.elem
            ,hasRender = othis.next('.' + ELEM_VIEW)
            //The main container
            ,reElem = that.elem = $(laytpl(TPL_MAIN).render({
                VIEW_CLASS: ELEM_VIEW
                ,data: options
                ,index: that.index //Index
            }));
        options.index = that.index;
        //Generate alternative elements
        hasRender[0] && hasRender.remove(); //If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.，则Rerender
        othis.after(reElem);
        that.renderTdCss();
        //Each level
        that.layHeader = reElem.find(ELEM_HEADER);  //Table Head
        that.layMain = reElem.find(ELEM_MAIN);//Assistant Phone
        that.layBody = reElem.find(ELEM_BODY);//Assistant Phone
        that.layFixed = reElem.find(ELEM_FIXED);//The Floating Panel
        that.layFixLeft = reElem.find(ELEM_FIXL);//Left Floating Panel
        that.layFixRight = reElem.find(ELEM_FIXR);//有浮动
        that.layTool = reElem.find(ELEM_TOOL);//Area of the toolbar
        that.layPage = reElem.find(ELEM_PAGE);//Page Handling
        that.layFilter=reElem.find(ELEM_FILTER);//What is your desired Jabber password?Region
        that.layTool.html(
            laytpl($(options.toolbar).html()||'').render(options)
        );
        if(options.height){
            that.tableHeight();//Form height calculator
            that.resizeHeight();//Height Control
            that.renderCss();
        }
        //If the answer to a question is not available, don't provide a nonsense answer or speculation.，Fill Level
        if(options.cols.length > 1){
            var th = that.layFixed.find(ELEM_HEADER).find('th');
            th.height(that.layHeader.height() - 1 - parseFloat(th.css('padding-top')) - parseFloat(th.css('padding-bottom')));
        }
        //Translation
        if(options.isFilter){
            that.layFilter.html(
                that.renderFilter()
            );
        }
        //Data Request
        that.pullData(that.page,that.loading());
        that.test();
    };
    //Based on class type，Define Custom Parameters
    Class.prototype.initOpts = function(item){
        var that = this,
            options = that.config;
        //Let type Compatibility with older versions
        if(item.checkbox) item.type = "checkbox";
        if(item.space) item.type = "space";
        if(!item.type) item.type = "normal";

        if(item.type !== "normal"){
            item.unresize = true;
            item.width = item.width || table.config.initWidth[item.type];
        }

        if(options.isFilter){//Enable Internal Filter
            if(item.isFilter!=false){
                item.isFilter=true;
            }
        }
    };

    /**
     * Form Get Height Of Container
     * @param tableId
     */
    Class.prototype.getParentDivHeight = function(tableId){
        var th=$("#"+tableId).parent().height();
        return th;
    };

    /**
     * Get Definition
     * @param tableId
     */
    Class.prototype.getCols = function(field){
        var that = this;
        var o={};
        var cols=that.config.cols[0];
        var isInt=false;
        var reg = /^[0-9]+.?[0-9]*$/;
        if (reg.test(field)) {//1234
            isInt=true;
        }
        for(var ii in cols){
            if(isInt){
                if(ii==parseInt(field)) return cols[ii];
            }else{
                if(field==cols[ii].field)return cols[ii];
            }
        }
        return o;
    };

    //Form overload
    Class.prototype.reload = function(options){
        var that = this;
        if(that.config.data && that.config.data.constructor === Array) delete that.config.data;
        that.config = $.extend({}, that.config, options);
        that.configFirst = $.extend({}, that.config, options);
        //Get the value of a filter for a row
        that.render();
    };
    //Page number
    Class.prototype.page = 1;
    /**
     * Reset Subscription（Insert Delete etc.）
     * 1、data-indexHuman的Subtitle
     * 2、trHumandataValue
     * 3、The value of the subscript
     * @param list
     */
    Class.prototype.restNumbers=function(){
        var that = this
            ,options = that.config;
        var  trs=that.layBody.find('table tbody tr');
        var i=0;
        trs.each(function (o) {
            $(this).attr("data-index",i);
            $(this).find(".laytable-cell-numbers p").text(i+1);
            $(this).data('index',i);
            i++;
        });
    }

    /**
     * Configure KSpread...
     * Only run once per data load
     * query、reloadTime before executing
     * @param o
     */
    Class.prototype.resetData=function(n) {
        var that = this
            ,options = that.config;
        if(options.isTree){
            if(!n.hasOwnProperty(table.config.cols.isOpen)){//If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.true
                n[table.config.cols.isOpen]=options.isOpenDefault;
            }
            if(!n.hasOwnProperty(table.config.cols.isShow)){//If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.true
                n[table.config.cols.isShow]=options.isOpenDefault?true:false;
            }
        }
        //Prohibit Setup
        if(!n.hasOwnProperty(table.config.cols.cheDisabled)){//Default tofalse
            n[table.config.cols.cheDisabled]=false;
        }
        //Record disallow multiple choice、Record count:
        if(n[table.config.cols.cheDisabled])options.cheDisabledNum++;
        if(n[table.config.cols.radDisabled])options.radDisabledNum++;
        n.children=[];
    }
    /**
     * BuildmapData
     * @param list
     * @return {{}}
     */
    Class.prototype.resetDataMap=function(list) {
        var that = this
            ,options = that.config;
        var field_Id=options.idField;
        var map={};
        if(list){
            list.forEach(function (o) {
                map[o[field_Id]]=o;
            });
        }
        return map;
    }
    Class.prototype.resetDataresetRoot=true;//Confirm reassignment of root node
    /**
     * Confirm root nodeid(Log back into the root node)
     */
    Class.prototype.resetDataRoot=function (list) {
        var that = this
            ,options = that.config;
        var field_Id=options[TREE_ID];
        var field_upId=options[TREE_UPID];
        var map=table.getDataMap(that.config.id);//Listmap，fieldId为key  //1 hour before appointmentmapData Set
        var rootList=table.cache[options.id].data.upIds||[];//根节点listConversation
        var rootMap={};//根节点mapConversation
        table.cache[options.id].data.upIds=[];
        rootList=table.cache[options.id].data.upIds;
        for(var i=0;i<list.length;i++){
            var temo=list[i];
            if(!map[temo[field_upId]]){//Could not find the drive
                if(!rootMap[temo[field_upId]]){//What is your name?
                    var temis=true;
                    rootList.forEach(function (temoo) {
                        if(temoo===temo[field_upId])temis=false;
                    });
                    if(temis)rootList.push(temo[field_upId]);
                }
                rootMap[temo[field_upId]]=temo[field_upId];
            }
        }
        return rootList;
    }
    /**
     * Collapse All Groups
     * 1、Original List Data
     * 2、Collection of Root Nodes
     */
    Class.prototype.resetDataTreeList=function (list, rootList) {
        var that = this
            ,options = that.config;
        var field_Id=options[TREE_ID];
        var field_upId=options[TREE_UPID];
        var treeList=[];
        //Collapse All Groups
        var fa = function(upId) {
            var _array = [];
            for (var i = 0; i < list.length; i++) {
                var n = list[i];
                if (n[field_upId] === upId) {
                    n.children = fa(n[field_Id]);
                    _array.push(n);
                }
            }
            return _array;
        }
        rootList.forEach(function (temo) {
            var temTreeObj=fa(temo);//递归
            if(temTreeObj){
                temTreeObj.forEach(function (o) {
                    treeList.push(o);
                });
            }
        });
        return treeList;
    }

    /**
     * Processing data list structure
     * 1、树结构
     */
    Class.prototype.resetDataTableList=function (treeList) {
        var that = this
            ,options = that.config;
        var field_Id=options[TREE_ID];
        var field_upId=options[TREE_UPID];
        var tableList=[];
        //Form action
        var fa2=function (l,level) {
            for (var i = 0; i < l.length; i++) {
                var n = l[i];
                n[table.config.cols.level]=level;//Set current level
                tableList.push(n);
                if (n.children&&n.children.length>0) {
                    fa2(n.children,1+level);
                }
            }
            return;
        }
        fa2(treeList,1);

        //1 hour before appointmentisOpen 和is_showState
        tableList.forEach(function (o) {
            var uo=that.treeFindUpData(o);
            if(!uo||(uo[table.config.cols.isOpen]&&uo[table.config.cols.isShow])){//No father：Answer；Dad mode enabled（Status Tracking：Answer；Hide Status：Hide）
                o[table.config.cols.isShow]=true;
            }else{
                o[table.config.cols.isShow]=false;
            }
        });
        return tableList;
    }

    /**
     * Reset current form data（1、List Itemize；2Tree Table）
     * Convert list data to tree structure and complianttableAnswer the following questions as honestly as possible. If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
     * @param list          List Data
     * @param field_Id      Keywords
     * @param field_upId    Classify Message as & Spam
     * @returns {Array}     [0]Form list  [1]树形结构
     */
    Class.prototype.resetDatas=function(list) {
        //console.time("resetDatas");
        var that = this
            ,options = that.config;
        var field_Id=options[TREE_ID];
        var field_upId=options[TREE_UPID];
        var datas=[];
        var treeList=[];
        var tableList=list;
        var map=that.resetDataMap(list);//Listmap，fieldId为key  //1 hour before appointmentmapData Set
        datas.push(tableList);//table结构
        datas.push(treeList)//tree树结构
        datas.push(map)//dataData map结构
        //Please set it to something meaningful.
        table.setDataList(that.config.id,tableList);
        table.setDataTreeList(that.config.id,treeList);
        table.setDataMap(that.config.id,map);
        if(list==null||list.length<=0)return datas;
        //Set Defaults
        for (var i = 0; i < list.length; i++) {
            that.resetData(list[i]);
        }
        if(options.isTree){//Tree
            tableList=[];
            table.setDataList(that.config.id,tableList);
            var rootList=table.cache[options.id].data.upIds||[];//根节点listConversation
            if(rootList.length<=0||that.resetDataresetRoot){//Confirm root node
                table.cache[options.id].data.upIds=[];
                rootList=that.resetDataRoot(list);
                that.resetDataresetRoot=false;
            }
            treeList=that.resetDataTreeList(list,rootList);
            table.setDataTreeList(that.config.id,treeList);//Set Tree Structure to Cache
            tableList=that.resetDataTableList(treeList);
            table.setDataList(that.config.id,tableList);//Set the data list structure to cache
        }
        //console.timeEnd("resetDatas");
        return datas;
    }
    /**
     * Based onidGet Objects from Table Data
     * @param data
     * @param field_Id
     * @param field_upId
     * @returns {Array}
     */
    Class.prototype.treeFindDataById=function(u_Id) {
        var that = this
            ,options = that.config;
        var e=null;
        var list=table.getDataList(that.key);
        var key=options[TREE_ID];
        list.forEach(function (o) {
            if(o[key]==u_Id){
                e=o;
                return;
            }
        });
        return e;
    }
    /**
     * Get parent node
     * @param u_Id
     */
    Class.prototype.treeFindUpData=function(o){
        var uOjb=null;
        var that = this
            ,options = that.config;
        //Process parent
        var key=options[TREE_UPID];//The following sources are available:keyName
        var mapData=table.getDataMap(that.config.id);//Getmap形式对象集合
        uOjb=mapData[o[key]];
        return uOjb;
    }
    /**
     * Get all the ancestors of this node，Backlist(不包含自己)
     * @param u_Id
     */
    Class.prototype.treeFindUpDatas=function(o){
        var uOjb=null;
        var that = this
            ,options = that.config;
        var list=[];
        var temf=function (temo) {
            var uo=that.treeFindUpData(temo);
            if(uo){
                list.push(uo);
                temf(uo);
            }
        };
        temf(o);
        return list;
    }
    /**
     * 根据父idGet all leaf nodes(递归)
     * @param o Data Object
     * @return {string}
     */
    Class.prototype.treeFindSonData=function (data) {
        var objs=[];
        function f(o) {
            if(o.children.length>0){
                o.children.forEach(function (i) {
                    objs.push(i);
                    if(i.children.length>0){
                        f(i);
                    }
                });
            }
        }f(data);
        return objs;
    };
    /**
     * Show conversion preview
     * @param o             Data
     * @param fieldName     Tree View Column
     * @returns {string}
     */
    Class.prototype.treeConvertShowName=function (o) {
        var that = this
            ,options = that.config;
        var isTreeNode=(o.children&&o.children.length>0);
        var temhtml='<div style="float: left;height: 28px;line-height: 28px;padding-left: '+
            function () {
                if(isTreeNode){
                    return '5px'
                }else{
                    return '21px'
                }
            }()
            +'">'
            +function () {//Locative
                var nbspHtml="<i>"//One Suit
                for(var i=1;i<o[table.config.cols.level];i++) {
                    nbspHtml = nbspHtml + "&nbsp;&nbsp;&nbsp;&nbsp;";
                }
                nbspHtml=nbspHtml+"</i>";
                return nbspHtml;
            }()
            +function () {//The icon or emblem
                var temTreeHtml='';
                var temTreeIsOpen=o[table.config.cols.isOpen]?"&#xe625;":"&#xe623;";
                if(isTreeNode){//The following sources are available:
                    temTreeHtml='<i class="layui-icon layui-tree-head">'+temTreeIsOpen+'</i>'
                        +that.treeIconRender(o,true);
                }else{//叶子节点
                    temTreeHtml+=that.treeIconRender(o,true);
                }
                return temTreeHtml;
            }()
            +'</div>';
        return temhtml;
    };
    /**
     * Expand or contract nodes
     * @param o         Question data（Tree Table）
     * @param isOpen    Expand（true）Or Fold（false）
     *
     * Each node has two states，
     * 1、Open Status（isOpen）   Open Status can be controlled by a single click，Other times do not require a change
     * 2、Status Tracking（Show or hide） Status is controlled by the parent node，Show the parent node if it is expanded or not，Otherwise do not show，But doesn't affect the open state
     */
    Class.prototype.treeNodeOpen=function (o,isOpen) {
        var that = this
            ,tr = that.layBody.find('tr[data-index="'+ o[table.config.indexName] +'"]');
        if(!o){
            return
        }
        o[table.config.cols.isOpen]=isOpen;
        //Collapse All Groups
        var fa = function(e) {
            if(e.children&&e.children.length>0){
                var temList=e.children;
                for (var i = 0; i < temList.length; i++) {
                    var n = temList[i];
                    if(o[table.config.cols.isOpen]){//Open Status，Off
                        if(e[table.config.cols.isOpen]&&e[table.config.cols.isShow]){//This node displays
                            var temo=that.layBody.find('tr[data-index="'+ n[table.config.indexName] +'"]');
                            temo.css('display', '');
                            n[table.config.cols.isShow]=true;
                        }
                    }else{
                        var temo=that.layBody.find('tr[data-index="'+ n[table.config.indexName] +'"]');
                        temo.css('display', 'none');
                        n[table.config.cols.isShow]=false;
                    }
                    fa(n);
                }
            }
        }
        fa(o);
        //Icon Theme Name
        that.treeIconRender(o,false);
        var dbClickI=tr.find('.layui-tree-head');
        if(o[table.config.cols.isOpen]){//Open Status
            dbClickI.html('&#xe625;');
        }else{
            dbClickI.html('&#xe623;');
        }
    };

    /**
     * iconဂ
     * @param o
     * @param isHtml  true（Backhtml）  false（Answer: Now Draw）
     */
    Class.prototype.treeIconRender=function (o,isHtml) {
        var that = this
            ,options = that.config
            ,iconOpen=options.iconOpen
            ,isTreeNode=(o.children&&o.children.length>0);
        var temTreeHtml='';
        if(iconOpen){
            var temf=function () {//自定义图标
                var temhtml='<i class="layui-tree-'+o[options.idField]+'" style="display:inline-block;width: 16px;height: 16px;background:url(';
                if(isTreeNode){//The following sources are available:
                    if(o[table.config.cols.isOpen]){
                        temhtml+=o[table.config.cols.iconOpen];
                    }else{
                        temhtml+=o[table.config.cols.iconClose];
                    }
                }else{
                    temhtml+=o[table.config.cols.icon];
                }
                temhtml+=') 0 0 no-repeat;"></i>';
                return temhtml;
            }
            if(isTreeNode){//The following sources are available:
                if((o[table.config.cols.iconOpen]||o[table.config.cols.iconClose])){
                    temTreeHtml=temf();
                }else{
                    temTreeHtml='<i class="layui-icon layui-tree-'+o[options.idField]+' layui-tree-'+ (o[table.config.cols.isOpen] ? "branch" : "leaf") +'" '+iconOpen+'>'+(o[table.config.cols.isOpen]?that.config.branch[1]:that.config.branch[0])+'</i>';
                }
            }else{//叶子节点
                if(o[table.config.cols.icon]){
                    temTreeHtml=temf();
                }else{
                    temTreeHtml+='<i class="layui-icon layui-tree-'+o[options.idField]+' layui-tree-leaf"  '+iconOpen+'>'+that.config.leaf+'</i>';
                }
            }
            if(isHtml){
                return temTreeHtml;
            }else{
                var temdiv=that.layBody.find('tr[data-index="'+ o[table.config.indexName] +'"]').find('td[data-field='+options[TREE_SHOW_NAME]+']').find('.layui-table-cell');
                $(temdiv).find('div .layui-tree-'+o[options.idField]).remove();//Add to Clipboarddiv
                $(temdiv).find('div').append(temTreeHtml);
            }
        }else{
            return temTreeHtml;
        }
    }
    //Get Data
    Class.prototype.pullData = function(curr, loadIndex){
        var that = this
            ,options = that.config
            ,request = options.request
            ,response = options.response
            ,sort = function(){
                if(typeof options.initSort === 'object'){
                    that.sort(options.initSort.field, options.initSort.type);
                }
            };
        that.startTime = new Date().getTime(); //Assistant Phone
        if(options.url){ //AjaxRequest
            var params = {};
            params[request.pageName] = curr;
            params[request.limitName] = options.limit;
            that.filterRulesSet(params);//What is your desired Jabber password?
           // that.sortSet(params);//Sort Conditions
            $.ajax({
                type: options.method || 'get'
                ,url: options.url
                ,data: $.extend(params, options.where)
                ,dataType: 'json'
                ,success: function(res){
                    if(!res[response.dataName]){//Return is undefined or not a functionnullTime Calculators[]
                        res[response.dataName]=[]
                        res[response.statusName]=0;
                        res[response.countName]=0;
                        res[response.msgName]='The data status returned was unexpected';
                    };
                    that.resetDataresetRoot=true;
                    //If there is a data analysis callback，Then get its return data
                    if(typeof options.parseData === 'function'){
                        res = options.parseData(res) || res;
                    }
                    that.resetDatas(res[response.dataName]);
                    res[response.dataName]=table.getDataList(options.id);
                    if(res[response.statusName] != response.statusCode){
                        that.renderForm();
                        that.layMain.html('<div class="'+ NONE +'">'+ (res[response.msgName] || 'The data status returned was unexpected') +'</div>');
                    } else {
                        that.renderData(res, curr, res[response.countName]);
                        options.time = (new Date().getTime() - that.startTime) + ' ms'; //耗时（Interface Request+3D-depth:）
                    }
                    loadIndex && layer.close(loadIndex);
                    that.events();
                    typeof options.done === 'function' && options.done(res, curr, res[response.countName]);
                }
                ,error: function(e, m){
                    that.layMain.html('<div class="'+ NONE +'">Data interface request exception</div>');
                    that.renderForm();
                    loadIndex && layer.close(loadIndex);
                }
            });
        } else if(options.data && options.data.constructor === Array){ //Data known
            var res = {},startLimit = curr*options.limit - options.limit
            res[response.dataName] = options.data.concat().splice(startLimit, options.limit);
            res[response.countName] = options.data.length;
            that.renderData(res, curr, options.data.length);
            that.events();
            typeof options.done === 'function' && options.done(res, curr, res[response.countName]);
        }
    };
    /**
     * Set Filtering Conditions
     */
    Class.prototype.filterRulesSet=function (p) {
        var that = this;
        p["filterRules"]=JSON.stringify(that.filterRules());
    }
    /**
     * Getting filter conditions
     * filterRules:
     * [
     * {"field":"XXXX","op":"equals","value":["1"],"datatype":"array"}
     * ,{"field":"XXXX","op":"contains","value":"3","datatype":"string"}
     * ]
     */
    Class.prototype.filterRules=function () {
        var that = this;
        var filterRules=[];
        //What is your desired Jabber password?
        var list=that.layFilter.find("[name^='filter_']");
        layui.each(list,function (i, o) {
            if($(o).val()){
                var tem={
                    "field":o.name
                    ,"op":"like"
                    ,"value":$(o).val()
                    ,"datatype":"string"
                }
                filterRules.push(tem);
            }
        });
        // console.log(filterRules,filterRules.toString(),JSON.stringify(filterRules));
        return filterRules;
    }

    //Table Row
    Class.prototype.eachCols = function(callback){
        var cols = $.extend(true, [], this.config.cols)
            ,arrs = [], index = 0;

        //Reorganize table structure
        layui.each(cols, function(i1, item1){
            layui.each(item1, function(i2, item2){
                //If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.，Then capture the corresponding sublist
                if(item2.colspan > 1){
                    var childIndex = 0;
                    index++
                    item2.CHILD_COLS = [];
                    layui.each(cols[i1 + 1], function(i22, item22){
                        if(item22.PARENT_COL || childIndex == item2.colspan) return;
                        item22.PARENT_COL = index;
                        item2.CHILD_COLS.push(item22);
                        childIndex = childIndex + (item22.colspan > 1 ? item22.colspan : 1);
                    });
                }
                if(item2.PARENT_COL) return; //If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.，Do not add a meeting request，Because it is stored in the parent column
                arrs.push(item2)
            });
        });

        //Review List，If there are sublists，Then go to the message
        var eachArrs = function(obj){
            layui.each(obj || arrs, function(i, item){
                if(item.CHILD_COLS) return eachArrs(item.CHILD_COLS);
                callback(i, item);
            });
        };

        eachArrs();
    };
    /**
     * Special Characters
     * @param callback
     */
    Class.prototype.renderTreeConvertShowName = function(o){
        var that = this
            ,options = that.config
            ,m=options.elem
            ,hasRender = m.next('.' + ELEM_VIEW);
        var temhtml=that.treeConvertShowName(o);
        var temdiv=that.layBody.find('tr[data-index="'+ o[table.config.indexName] +'"]').find('td[data-field='+options[TREE_SHOW_NAME]+']').find('.layui-table-cell');
        $(temdiv).find('div').remove();
        $(temdiv).prepend(temhtml);
    }
    /**
     * Specify the column style(Width Style Setup)
     * @param callback
     */
    Class.prototype.renderTdCss = function(){
        var that = this
            ,options = that.config
            ,m=options.elem
            ,hasRender = m.next('.' + ELEM_VIEW);
        var id= that.index+"_"+MOD_NAME+'_td_style';
        hasRender.find("#"+id).remove();
        var styel='<style id="'+id+'">'
            +function () {
                var ret="";
                layui.each(that.config.cols,function (i1, item1) {
                    layui.each(item1, function(i2, item2){
                        ret+='.laytable-cell-'+that.index+'-'+(item2.field||i2)+'{' +
                            'width:'+(item2.width?item2.width+"px":"0px")
                            +'}';
                    });
                });
                return ret;
            }()+'</style>';
        hasRender.append(styel);
    }

    /**
     * 插件内css
     */
    Class.prototype.renderCss = function(){
        var that = this
            ,options = that.config
            ,m=options.elem
            ,hasRender = m.next('.' + ELEM_VIEW);
        var id=that.index+"_"+MOD_NAME+'_style';
        hasRender.find("#"+id).remove();
        var styel='<style id="'+id+'">'
            +function () {
                var ret=".layui-tree-head{cursor: pointer;}";//Click on icons
                ret+=".layui-table-view {margin:0;}";
                return ret;
            }()+'</style>';
        hasRender.append(styel);
    }

    /**
     * Generate Cells
     * @param obj       Number of characters
     * @param numbers   Subtitle
     * @param cols      List Definition Data
     * @param i3        Number of columns
     */
    Class.prototype.renderTrUpids=function (obj) {
        var that = this
            ,options = that.config;
        var tree_upid_key=options[TREE_UPID];
        var upids=' upids="'+obj["upIds"]+'" ';
        var u_id=' u_id="'+obj[tree_upid_key]+'" '
        var ret=options.isTree?u_id:'';
        return ret;
    }
    /**
     * Generate Cells
     * @param obj       Number of characters
     * @param numbers   Subtitle
     * @param cols      List Definition Data
     * @param i3        Number of columns
     */
    Class.prototype.renderTd=function (obj,cols,numbers,i3) {
        var that = this
            ,options = that.config;
        var v=obj[cols.field]==null?'':String(obj[cols.field]);

        var field = cols.field || i3, content = v
            ,cell = that.getColElem(that.layHeader, field);

        var treeImgHtml='';
        if(options.isTree){
            if(options.treeShowName==cols.field){
                treeImgHtml=that.treeConvertShowName(obj);
            }
        }
        //td内容
        var td = ['<td data-field="'+ field +'" '+ function(){
            var attr = [];
            if(cols.edit) attr.push('data-edit="'+ cols.edit +'"'); //Whether to allow editing of cells
            if(cols.align) attr.push('align="'+ cols.align +'"'); //Align Mode
            if(cols.templet) attr.push('data-content="'+ content +'"'); //Custom Template
            if(cols.toolbar) attr.push('data-off="true"'); //Custom Template
            if(cols.event) attr.push('lay-event="'+ cols.event +'"'); //Custom Event
            if(cols.style) attr.push('style="'+ cols.style +'"'); //Custom Style
            if(cols.minWidth) attr.push('data-minwidth="'+ cols.minWidth +'"'); //Minimum cell width
            return attr.join(' ');
        }() +'>'
            ,'<div class="layui-table-cell laytable-cell-'+ function(){ //Return the correspondingCSSClass Inheritance
                var str = (options.index + '-' + field);
                return cols.type === 'normal' ? str
                    : (str + ' laytable-cell-' + cols.type);
            }() +'">'+treeImgHtml+'<p style="width: auto;height: 100%;">'+ function(){
                var tplData = $.extend(true, {LAY_INDEX: numbers}, obj);
                //Show check boxes
                if(cols.type === 'checkbox'){
                    return tplData[table.config.cols.cheDisabled]?''
                        :'<input type="checkbox" name="'+TABLE_CHECKBOX_ID+'" value="'+tplData[table.config.indexName]+'" lay-skin="primary" '+ function(){
                        var isCheckName = table.config.cols.isCheckName;
                        //If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
                        if(cols[isCheckName]){
                            obj[isCheckName] = cols[isCheckName];
                            return cols[isCheckName] ? 'checked' : '';
                        }
                        return tplData[isCheckName] ? 'checked' : '';
                    }() +'>';
                } else if(cols.type === 'numbers'){ //Translation of the following text to english:  Description
                    return numbers;
                }else if(cols.type === 'drop'){//Pull down menu
                    var rowsField=dl.ui.table.drop.findFieldObj(options.cols[0],field);
                    if(rowsField&&rowsField['drop']){
                        var o=dl.cache.code.get(rowsField.drop);
                        return dl.ui.table.drop.findDropLable(rowsField.drop,content);
                    }
                }else if(cols.type === 'radio'){//Check to discard the answer
                    return tplData[table.config.cols.radDisabled]?''
                        :'<input type="radio" name="'+TABLE_RADIO_ID+'" '+function () {
                        var isRadio = table.config.cols.isRadio;
                        if(cols[isRadio]){
                            obj[isRadio] = cols[isRadio];
                            return cols[isRadio] ? 'checked' : '';
                        }
                        return tplData[isRadio] ? 'checked' : '';
                    }()+' value="'+tplData[table.config.indexName]+'" title=" ">';
                }

                //Analyzer Toolbar Template
                if(cols.toolbar){
                    return laytpl($(cols.toolbar).html()||'').render(tplData);
                }

                return cols.templet ? function(){
                    return typeof cols.templet === 'function'
                        ? cols.templet(tplData)
                        : laytpl($(cols.templet).html() || String(content)).render(tplData)
                }() : content;
            }()
            ,'</p></div></td>'].join('');
        return td;
    }
    /**
     * GeneratingtrAlways answer as helpfully as possible.
     * @param obj            Number of characters
     * @param numbers          Line Number
     */
    Class.prototype.renderTr=function (obj,numbers) {
        var that = this
            ,options = that.config;
        var tds= [];
        that.eachCols(function(i3, cols){//colsList Definition
            var field = cols.field || i3, content = obj[field];
            if(cols.colspan > 1) return;
            var td = that.renderTd(obj,cols,numbers,i3);//td内容
            tds.push(td);
            // if(item3.fixed && item3.fixed !== 'right') tds_fixed.push(td);
            // if(item3.fixed === 'right') tds_fixed_r.push(td);
        });
        return tds;
    };
    /**
     * Form input data part
     * @param res
     * @param curr
     * @param count
     * @param sort
     */
    Class.prototype.renderData = function(res, curr, count, sort){
        var that = this
            ,options = that.config
            ,data = res[options.response.dataName] || []
            ,trs = []
            ,trs_fixed = []
            ,trs_fixed_r = []
            //Drawable View
            ,render = function(){ //The follow-up performance improvement priority
                if(!sort && that.sortKey){
                    return that.sort(that.sortKey.field, that.sortKey.sort, true);
                }

                layui.each(data, function(i1, obj){
                    var uo=that.treeFindUpData(obj);
                    var display="";
                    if(!obj[table.config.cols.isShow]&&options.isTree){
                        display="display: none;";
                    }
                    var tds = [], tds_fixed = [], tds_fixed_r = []
                        ,numbers = i1 + options.limit*(curr - 1) + 1; //Serial Number
                    if(obj.length === 0) return;
                    if(!sort){
                        obj[table.config.indexName] = i1;
                    }
                    tds=that.renderTr(obj,numbers);
                    trs.push('<tr style="'+display+'" data-index="'+ i1 +'" '+that.renderTrUpids(obj)+'>'+ tds.join('') + '</tr>');
                    trs_fixed.push('<tr data-index="'+ i1 +'">'+ tds_fixed.join('') + '</tr>');
                    trs_fixed_r.push('<tr data-index="'+ i1 +'">'+ tds_fixed_r.join('') + '</tr>');
                });
                //if(data.length === 0) return;
                that.layBody.scrollTop(0);
                that.layMain.find('.'+ NONE).remove();
                that.layMain.find('tbody').html(trs.join(''));
                that.layFixLeft.find('tbody').html(trs_fixed.join(''));
                that.layFixRight.find('tbody').html(trs_fixed_r.join(''));
                that.renderForm();
                that.haveInit ? that.scrollPatch() : setTimeout(function(){
                    that.scrollPatch();
                }, 50);
                that.haveInit = true;
                layer.close(that.tipsIndex);
            };
        that.key = options.id || options.index;
        // table.cache[that.key] = data; //Record data
        table.setDataList(that.key,data);
        //Show hidden pages
        that.layPage[data.length === 0 && curr == 1 ? 'addClass' : 'removeClass'](HIDE);
        //Sort
        if(sort){
            return render();
        }
        if(data.length === 0){
            that.renderForm();
            that.layFixed.remove();
            that.layMain.find('tbody').html('');
            that.layMain.find('.'+ NONE).remove();
            return that.layMain.append('<div class="'+ NONE +'">'+(res[options.response.msgName]?res[options.response.msgName]:options.text.none)+'</div>');
        }
        render();
        that.renderPage(count);//Page Handling
        //calssLoading complete
        table.pushClassIds(options.id,true);
    };
    /**
     *  Report problem
     */
    Class.prototype.renderPage=function (count) {
        var that = this
            ,options = that.config;
        //Sticky Note Properties
        if(options.isPage){
            options.page = $.extend({
                elem: 'layui-table-page' + options.index
                ,count: count
                ,limit: options.limit
                ,limits: options.limits || [10,15,20,30,40,50,60,70,80,90]
                ,groups: 3
                ,layout: ['prev', 'page', 'next', 'skip', 'count', 'limit']
                ,prev: '<i class="layui-icon">&#xe603;</i>'
                ,next: '<i class="layui-icon">&#xe602;</i>'
                ,jump: function(obj, first){
                    if(!first){
                        //The page itself does not need to be updated，Syncing the following parameters，主要是因为其它处理统一用到了它们
                        //And not the one that is used to options.page 中的参数（Make sure that the pages are usable when the assistant is not started yet）
                        that.page = obj.curr; //Update bookmarks
                        options.limit = obj.limit; //Update every
                        that.pullData(obj.curr, that.loading());
                    }
                }
            }, options.page);
            options.page.count = count; //Update the total number of items
            laypage.render(options.page);
        }
    };
    /**
     * In the filtration area
     */
    Class.prototype.renderFilter = function(){
        var that = this
            ,options = that.config
            ,VIEW_CLASS=ELEM_VIEW
            ,index=that.index; //Index
        var v = [];
        v.push('<form method="post"  id="'+options.id+'_filter_form">');
        v.push('<table cellspacing="0" cellpadding="0" border="0" class="layui-table"><thead><tr>');
        layui.each(options.cols,function (i, o) {
            layui.each(o, function(i2, item2){
                var field=item2.field||i2;
                var minW=item2.minWidth?"data-minwidth='"+item2.minWidth+"'":"";
                var rowCols=item2.colspan?'colspan="'+item2.colspan+'"':'';
                var rowspan=item2.rowspan?'rowspan="'+item2.rowspan+'"':'';
                var unresize=item2.unresize?'data-unresize="true"':'';
                v.push('<th data-field="'+field+'"'+minW+rowCols+rowspan +unresize+'>');
                v.push('<div class="layui-table-cell laytable-cell-'+function () {
                    var tem="";
                    if (item2.colspan > 1) {
                        tem='group';
                    }else{
                        tem=index+"-"+field;
                        if(item2.type !== "normal"){
                            tem+=" laytable-cell-"+item2.type;
                        }
                    }
                    return tem;
                }()+'">');
                if(!item2.isFilter||!item2.field){//If you don't know the answer to a question, please don't share false information.
                    v.push('');
                }else{
                    v.push('<input class="layui-input '+ ELEM_EDIT +'" id="filter_'+item2.field+'" name="filter_'+item2.field+'">');
                }
                v.push('</div></th>');

            });
        });
        v.push('</tr></thead></table>');
        v.push('</form>');
        return v.join('');
    };
    //Find the saxophone
    Class.prototype.getColElem = function(parent, field){
        var that = this
            ,options = that.config;
        return parent.eq(0).find('.laytable-cell-'+ (options.index + '-' + field) + ':eq(0)');
    };
    //Form
    Class.prototype.renderForm = function(type){
        form.render(type, 'LAY-table-'+ this.index);
    }
    /**
     * Set Sort Parameters
     * @param p
     */
    Class.prototype.sortSet=function (p) {
        var that = this;
        var sort=[];
        var cols=that.config.cols[0];
        cols.forEach(function (t) {
            if(t.sortType){
                var tem={
                   "field":t.field
                    ,"sort":t.sortType
                }
                sort.push(tem);
            }
        });
        p.sort=JSON.stringify(sort);
    }
    /**
     * Set Sort Field
     * @param th
     * @param type
     * @param pull
     * @param formEvent
     */
    Class.prototype.sort = function(th, type, pull, formEvent){
        var that = this
            ,field
            ,res = {}
            ,options = that.config
            ,filter = options.elem.attr('lay-filter')
            ,data = table.getDataList(that.key), thisData;
        //Question:
        if(typeof th === 'string'){
            that.layHeader.find('th').each(function(i, item){
                var othis = $(this)
                    ,_field = othis.data('field');
                if(_field === th){
                    th = othis;
                    field = _field;
                    return false;
                }
            });
        }
        try {
            var field = field || th.data('field');
            //If the sort order specified is already in effect，Do not execute the child
            if(that.sortKey && !pull){
                if(field === that.sortKey.field && type === that.sortKey.sort){
                    return;
                }
            }
            var elemSort = that.layHeader.find('th .laytable-cell-'+ options.index +'-'+ field).find(ELEM_SORT);
            //that.layHeader.find('th').find(ELEM_SORT).removeAttr('lay-sort'); //Clear other title sort state
            elemSort.attr('lay-sort', type || null);
            that.layFixed.find('th')
        } catch(e){
            return hint.error('Table modules: Did not match to field');
        }
       /* //Record index and type sorting
        that.sortKey = {
            field: field
            ,sort: type
        };
        if(type === 'asc'){ //Incremental
        } else if(type === 'desc'){ //10th
            thisData = layui.sort(data, field, true);
        } else { //Clear Sort
            thisData = layui.sort(data, table.config.indexName);
        }
        */
        var cols=that.getCols(field);
        if(cols){
            cols.sortType=type
        }
    };
    //Requestloading
    Class.prototype.loading = function(){
        var that = this
            ,options = that.config;
        if(options.loading && options.url){
            return layer.msg('Data Requests', {
                icon: 16
                ,offset: [
                    that.elem.offset().top + that.elem.height()/2 - 35 - _WIN.scrollTop() + 'px'
                    ,that.elem.offset().left + that.elem.width()/2 - 90 - _WIN.scrollLeft() + 'px'
                ]
                ,time: -1
                ,anim: -1
                ,fixed: false
            });
        }
    };
    //Synchronize selection values
    Class.prototype.setCheckData = function(index, checked){
        var that = this
            ,options = that.config
            ,thisData = table.getDataList(that.key);
        if(!thisData[index]) return;
        if(thisData[index].constructor === Array) return;
        thisData[index][table.config.cols.isCheckName] = checked;
    };
    //Sticky Note Properties
    Class.prototype.syncCheckAll = function(){
            var that = this
                ,options = that.config;
            var list=table.getDataList(that.config.id);
            if(!list)return;
            var temis=true;//Select All
            var checkNum=0;//Select the number of the answer
            list.forEach(function (t) {
                if(!t[table.config.cols.cheDisabled]){
                    if(t[table.config.cols.isCheckName]){
                        var  checkAllElem = that.layBody.find('tr[data-index='+t[table.config.indexName]+']').find('input[name="'+TABLE_CHECKBOX_ID+'"]');
                        checkAllElem.prop('checked', true);
                        checkNum++;
                    }else{
                        temis=false;
                        var  checkAllElem = that.layBody.find('tr[data-index='+t[table.config.indexName]+']').find('input[name="'+TABLE_CHECKBOX_ID+'"]');
                        checkAllElem.prop('checked', false);
                    }
                }
            });
            if(temis){//Set All
                var  checkAllElem = that.layHeader.find('input[name="'+TABLE_CHECKBOX_ID+'"]');
                checkAllElem.prop('checked', true);
            }
            if(checkNum<(list.length-options.cheDisabledNum)){
                var  checkAllElem = that.layHeader.find('input[name="'+TABLE_CHECKBOX_ID+'"]');
                checkAllElem.prop('checked', false);
            }
        // console.time("pullData");
        // console.timeEnd("pullData");
        that.renderForm('checkbox');
    };
    //GetcssRule
    Class.prototype.getCssRule = function(field, callback){
        var that = this
            ,style = that.elem.find('style')[0]
            ,sheet = style.sheet || style.styleSheet || {}
            ,rules = sheet.cssRules || sheet.rules;
        layui.each(rules, function(i, item){
            if(item.selectorText === ('.laytable-cell-'+ that.index +'-'+ field)){
                return callback(item), true;
            }
        });
    };

    Class.prototype.test = function(){
    }
    /**
     * Form changes auto-adapt
     */
    Class.prototype.resize = function(){
        var that = this;
        //Set height based on parenttableof Hedar
        // 1、tableSelf-Tier Container Height（layui-table-view）
        // 2、Assistant Phone（layui-table-main）
        that.columnWidthInit();//Width Calculator
        that.tableHeight();//Form height calculator
        that.resizeHeight();//Height Control
        that.resizeWidth();//Width Control
    };

    //Dynamic column width assignment
    Class.prototype.setArea = function(){
        var that = this;
        that.columnWidthInit();//Width Calculator
        that.tableHeight();//Form height calculator
    };
    /**
     * Width Calculator
     */
    Class.prototype.columnWidthInit = function(){
        var that = this,
            options = that.config
            ,colNums = 0 //Number of spaces
            ,autoColNums = 0 //The number of columns in a custom game
            ,autoWidth = 0 //The width of the column
            ,countWidth = 0 //All Columns Width And
            ,cntrWidth = options.width ||function(){ //Get the width of the container
                //If the width of the parent element is0（Generally hidden elements），Then continue searching for the upper element，Until we find out the real width
                var getWidth = function(parent){
                    var width, isNone;
                    parent = parent || options.elem.parent()
                    width = parent.width();
                    try {
                        isNone = parent.css('display') === 'none';
                    } catch(e){}
                    if(parent[0] && (!width || isNone)) return getWidth(parent.parent());
                    return width;
                };
                return getWidth();
            }()-17;
        //Statistics column number
        that.eachCols(function(){
            colNums++;
        });
        //Remove Border
        cntrWidth = cntrWidth - function(){
            return (options.skin === 'line' || options.skin === 'nob') ? 2 : colNums + 1;
        }();

        //All history
        layui.each(options.cols, function(i1, item1){
            layui.each(item1, function(i2, item2){
                var width;
                if(!item2){
                    item1.splice(i2, 1);
                    return;
                }
                that.initOpts(item2);
                width = item2.width || 0;
                if(item2.colspan > 1) return;
                if(/\d+%$/.test(width)){
                    item2.width = width = Math.floor((parseFloat(width) / 100) * cntrWidth);
                } else if(item2._is_width_dev||!width){ //Column width is empty
                    item2._is_width_dev=true;//Use default column width
                    item2.width = width = 0;
                    autoColNums++;
                }
                countWidth = countWidth + width;
            });
        });
        that.autoColNums = autoColNums; //Record & automatically counted
        //If true，Then share the rest equally。Otherwise，Add a default width to unset columns
        (cntrWidth > countWidth && autoColNums) && (
            autoWidth = (cntrWidth - countWidth) / autoColNums
        );
        layui.each(options.cols, function(i1, item1){
            layui.each(item1, function(i2, item2){
                var minWidth = item2.minWidth || options.cellMinWidth;
                if(item2.colspan > 1) return;
                if(item2.width === 0){
                    item2.width = Math.floor(autoWidth >= minWidth ? autoWidth : minWidth); //Do not translate the following phrases:
                }
            });
        });
    };
    /**
     * Re-draw width
     */
    Class.prototype.resizeWidth = function(){
        var that = this;
        that.renderTdCss();
    };
    /**
     * Form height calculator
     */
    Class.prototype.tableHeight = function(){
        var that = this,
            options = that.config,
            optionsFirst = that.configFirst;
        //Confirm Height
        if(!table.kit.isNumber(optionsFirst.height)){//If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.，Please calculate the height
            var htremove=0;//Remove Height
            if(options.heightRemove&&table.kit.isArray(options.heightRemove)){
                var htatt=options.heightRemove;
                htatt.forEach(function (t) {
                    var temh=table.kit.isNumber(t)?t:$(t).outerHeight(true);
                    if(table.kit.isNumber(temh)){
                        htremove+=temh;
                    }
                });
            }
            //Height Full：full-& Gender:
            var th=_WIN.height()-htremove-1;//that.getParentDivHeight(options.id);
            that.fullHeightGap=0;
            if(options.height){
                if(/^full-\d+$/.test(options.height)){
                    that.fullHeightGap = options.height.split('-')[1];
                }
            }
            options.height = th - that.fullHeightGap;
        }
    };
    /**
     * Redraw height
     */
    Class.prototype.resizeHeight = function(){
        var that = this
            ,options = that.config
            ,height = options.height, bodyHeight;
        if(height < 135) height = 135;
        that.elem.css('height', height);
        //tbodyRegional height
        // bodyHeight = parseFloat(height) - parseFloat(that.layHeader.height()) - 1;//Code Fork
        var theader=options.isFilter?76:38;//There is no inner filter area
        bodyHeight = parseFloat(height) - theader - 1;//###Attention：Now set the fixed height of the death note to38px，Supports multiple table heads（OntabCannot get correct height from mode，1 week）
        if(options.toolbar){
            bodyHeight = bodyHeight - that.layTool.outerHeight();
        }
        if(options.isPage){
            bodyHeight = bodyHeight - that.layPage.outerHeight() - 1;
        }
        that.layMain.css('height', bodyHeight);
    };
    //Get Scrollbar Width
    Class.prototype.getScrollWidth = function(elem){
        var width = 0;
        if(elem){
            width = elem.offsetWidth - elem.clientWidth;
        } else {
            elem = document.createElement('div');
            elem.style.width = '100px';
            elem.style.height = '100px';
            elem.style.overflowY = 'scroll';

            document.body.appendChild(elem);
            width = elem.offsetWidth - elem.clientWidth;
            document.body.removeChild(elem);
        }
        return width;
    };
    //Add to Dictionary
    Class.prototype.scrollPatch = function(){
        var that = this
            ,layMainTable = that.layMain.children('table')
            ,scollWidth = that.layMain.width() - that.layMain.prop('clientWidth') //Vertical Scrollbar Width
            ,scollHeight = that.layMain.height() - that.layMain.prop('clientHeight') //Vertical Scrollbar Height
            ,getScrollWidth = that.getScrollWidth(that.layMain[0]) //Get the width of the main container scrollbar，If you don't know the answer to a question, please don't share false information.
            ,outWidth = layMainTable.outerWidth() - that.layMain.width(); //Form field width exceeded

        //If auto-width is true，Then make sure to absolutely fill it up，And you can't have a horizontal scroll bar appear
        if(that.autoColNums && outWidth < 5 && !that.scrollPatchWStatus){
            var th = that.layHeader.eq(0).find('thead th:last-child')
                ,field = th.data('field');
            that.getCssRule(field, function(item){
                var width = item.style.width || th.outerWidth();
                item.style.width = (parseFloat(width) - getScrollWidth - outWidth) + 'px';
                //Secondary check，If the vertical scroll bar is still visible
                if(that.layMain.height() - that.layMain.prop('clientHeight') > 0){
                    item.style.width = parseFloat(item.style.width) - 1 + 'px';
                }
                that.scrollPatchWStatus = true;
            });
        }
        if(scollWidth && scollHeight){
            if(that.elem.find('.layui-table-patch').length<=0){
                var patchElem = $('<th class="layui-table-patch"><div class="layui-table-cell"></div></th>'); //Element Patch
                patchElem.find('div').css({
                    width: scollWidth
                });
                that.layHeader.eq(0).find('thead tr').append(patchElem);
                //that.layFilter.find('table thead tr').append(patchElem);
            }
        } else {
            that.layFilter.eq(0).find('.layui-table-patch').remove();
            that.layHeader.eq(0).find('.layui-table-patch').remove();
        }
        //Fixed column height
        var mainHeight = that.layMain.height()
            ,fixHeight = mainHeight - scollHeight;
        that.layFixed.find(ELEM_BODY).css('height', layMainTable.height() > fixHeight ? fixHeight : 'auto');
        //Form width is less than container width，Hide fixed column
        that.layFixRight[outWidth > 0 ? 'removeClass' : 'addClass'](HIDE);
        //操作栏
        that.layFixRight.css('right', scollWidth - 1);
    };
    //Event Handling
    Class.prototype.events = function(){
        var that = this
            ,options = that.config
            ,_BODY = $('body')
            ,dict = {}
            ,th = that.layHeader.find('th')
            ,bodytr=that.layBody.find('tr')
            ,resizing;
        //Click on Command
        bodytr.unbind('click').on('click',function (e) {
            var index=$(this).attr("data-index");
            var list=table.getDataList(that.config.id);
            var o=list[index];
            typeof options.onClickRow === 'function' && options.onClickRow(index,o);
        });
        //row double click event
        bodytr.unbind('dblclick').on('dblclick',function (e) {
            var index=$(this).attr("data-index");
            var list=table.getDataList(that.config.id);
            var o=list[index];
            typeof options.onDblClickRow === 'function' && options.onDblClickRow(index,o);
        });
        //Adjust Width
        th.unbind('mousemove').on('mousemove', function(e){
            var othis = $(this)
                ,oLeft = othis.offset().left
                ,pLeft = e.clientX - oLeft;
            if(othis.attr('colspan') > 1 || othis.data('unresize') || dict.resizeStart){
                return;
            }
            dict.allowResize = othis.width() - pLeft <= 10; //Are we in a drag allow zone
            _BODY.css('cursor', (dict.allowResize ? 'col-resize' : ''));
        })
        th.unbind('mouseleave').on('mouseleave', function(){
            var othis = $(this);
            if(dict.resizeStart) return;
            _BODY.css('cursor', '');
        })
        th.unbind('mousedown').on('mousedown', function(e){
            var othis = $(this);
            if(dict.allowResize){
                var field = othis.data('field');
                e.preventDefault();
                dict.resizeStart = true; //Start dragging
                dict.offset = [e.clientX, e.clientY]; //Record initial coordinates

                that.getCssRule(field, function(item){
                    var width = item.style.width || othis.outerWidth();
                    dict.rule = item;
                    dict.ruleWidth = parseFloat(width);
                    dict.minWidth = othis.data('minwidth') || options.cellMinWidth;
                });
            }
        });
        //拖拽中
        _DOC.unbind('mousemove').on('mousemove', function(e){
            if(dict.resizeStart){
                e.preventDefault();
                if(dict.rule){
                    var setWidth = dict.ruleWidth + e.clientX - dict.offset[0];
                    if(setWidth < dict.minWidth) setWidth = dict.minWidth;
                    dict.rule.style.width = setWidth + 'px';
                    layer.close(that.tipsIndex);
                }
                resizing = 1
            }
        })
        _DOC.unbind('mouseup').on('mouseup', function(e){
            if(dict.resizeStart){
                dict = {};
                _BODY.css('cursor', '');
                that.scrollPatch();
            }
            if(resizing === 2){
                resizing = null;
            }
        });
        //Sort
        th.unbind('click').on('click', function(){//Click on title bar
            var othis = $(this)
                ,elemSort = othis.find(ELEM_SORT)
                ,nowType = elemSort.attr('lay-sort')
                ,type;

            if(!elemSort[0] || resizing === 1) return resizing = 2;

            if(nowType === 'asc'){
                type = 'desc';
            } else if(nowType === 'desc'){
                type = null;
            } else {
                type = 'asc';
            }
            that.sort(othis, type, null, true);
            table.query(that.key);//From New
        })
        th.find(ELEM_SORT+' .layui-edge ').unbind('click').on('click', function(e){//Click on the triangles
            var othis = $(this)
                ,index = othis.index()
                ,field = othis.parents('th').eq(0).data('field')
            layui.stope(e);
            if(index === 0){
                that.sort(field, 'asc', null, true);
            } else {
                that.sort(field, 'desc', null, true);
            }
            table.query(that.key);//From New
        });
        if(!that.eventsinitIsRun){
            that.eventsinit();
            that.eventsinitIsRun=true;
        }
        //Sync Scrollbar
        that.layMain.unbind('scroll').on('scroll', function(){
            var othis = $(this)
                ,scrollLeft = othis.scrollLeft()
                ,scrollTop = othis.scrollTop();

            that.layHeader.scrollLeft(scrollLeft);
            that.layFilter.scrollLeft(scrollLeft);
            that.layFixed.find(ELEM_BODY).scrollTop(scrollTop);

            layer.close(that.tipsIndex);
        });
        _WIN.unbind('resize').on('resize', function(){ //Self-adaptive
            that.resize();
        });
    };
    //Event Processor Cell(Only run once)
    Class.prototype.eventsinitIsRun=false;
    //Next:
    Class.prototype.eventsinit = function(){
        var that = this
            ,options = that.config
            ,ELEM_CELL = '.layui-table-cell'
            ,filter = options.elem.attr('lay-filter');
        //Filter Text
        that.layFilter.on('keyup',"[name^='filter_']",function () {
            that.page=1;
            that.pullData(that.page, that.loading());
        });
        //Action Events
        that.layBody.on('mouseenter','tr',function(){
            var othis = $(this)
                ,index = othis.index();
            that.layBody.find('tr:eq('+ index +')').addClass(ELEM_HOVER)
        })
        that.layBody.on('mouseleave','tr', function(){
            var othis = $(this)
                ,index = othis.index();
            that.layBody.find('tr:eq('+ index +')').removeClass(ELEM_HOVER)
        });
        //Cell Events
        that.layBody.on('click','td div.layui-table-cell p',function(){
            var othis = $(this).parent().parent()
                ,field = othis.data('field')
                ,editType = othis.data('edit')
                ,index = othis.parents('tr').eq(0).data('index')
                ,data = table.getDataList(that.key)[index]
                ,elemCell = othis.children(ELEM_CELL);
            var  options = that.config;
            layer.close(that.tipsIndex);
            if(othis.data('off')) return;

            //Show the side bar
            if(editType){
                if(editType === 'select') { //Select Frame
                    var dropName=othis.data('drop');
                    var rowsField=dl.ui.table.drop.findFieldObj(options.cols[0],field);
                    var o=dl.cache.code.get(rowsField.drop);
                    var html='';
                    var scv=o.syscodevaluecache;
                    for(var i in scv){
                        var isSelected="";
                        if(scv[i].scv_value==data[field]){
                            isSelected="selected='selected'";
                        }
                        //Select
                        html+='<option '+isSelected+'  value="'+scv[i].scv_value+'">'+scv[i].scv_show_name+'</option>'
                    }
                    var select = $('<select class="'+ ELEM_EDIT +'" lay-ignore>' +
                        html+
                        '</select>');
                    othis.find('.'+ELEM_EDIT)[0] || othis.append(select);
                } else { //输入框
                    var input = $('<input class="layui-input '+ ELEM_EDIT +'">');
                    input[0].value = $.trim($(this).text());//  othis.data('content') || elemCell.text();
                    othis.find('.'+ELEM_EDIT)[0] || othis.append(input);
                    input.focus();
                }
                return;
            }

            //If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.，则可查看更多
            var c=that.getCols(field);

            if(!table.config.initWidth[c["type"]]){
                if(elemCell.find('.layui-form-switch,.layui-form-checkbox')[0]) return; //Restrict to one appearance（temporary）
                if(Math.round(elemCell.prop('scrollWidth')) > Math.round(elemCell.outerWidth())){
                    that.tipsIndex = layer.tips([
                        '<div class="layui-table-tips-main" style="margin-top: -'+ (elemCell.height() + 16) +'px;'+ function(){
                            if(options.size === 'sm'){
                                return 'padding: 4px 15px; font-size: 12px;';
                            }
                            if(options.size === 'lg'){
                                return 'padding: 14px 15px;';
                            }
                            return '';
                        }() +'">'
                        ,elemCell.html()
                        ,'</div>'
                        ,'<i class="layui-icon layui-table-tips-c">&#x1006;</i>'
                    ].join(''), elemCell[0], {
                        tips: [3, '']
                        ,time: -1
                        ,anim: -1
                        ,maxWidth: (device.ios || device.android) ? 300 : 600
                        ,isOutAnim: false
                        ,skin: 'layui-table-tips'
                        ,success: function(layero, index){
                            layero.find('.layui-table-tips-c').on('click', function(){
                                layer.close(index);
                            });
                        }
                    });
                }
            }
        });
        that.layBody.on('change','.'+ELEM_EDIT, function(){
            var othis = $(this)
                ,value = this.value
                ,field = othis.parent().data('field')
                ,index = othis.parents('tr').eq(0).data('index')
                ,data = table.getDataList(that.key)[index];
            data[field] = value; //Cache values
            layui.event.call(this, MOD_NAME, 'edit('+ filter +')', {
                value: value
                ,data: data
                ,field: field
            });
        });
        that.layBody.on('blur','.'+ELEM_EDIT, function(){//Lose focus
            var templet
                ,othis = $(this)
                ,field = othis.parent().data('field')
                ,index = othis.parents('tr').eq(0).data('index')
                ,editType = othis.parent().data('edit')
                ,data = table.getDataList(that.key)[index];
            var  options = that.config;
            that.eachCols(function(i, item){
                if(item.field == field && item.templet){
                    templet = item.templet;
                }
            });
            var value="";
            if(editType === 'select') { //Select Frame
                var rowsField=dl.ui.table.drop.findFieldObj(options.cols[0],field);
                if(rowsField&&rowsField['drop']){
                    var o=dl.cache.code.get(rowsField.drop);
                    value=dl.ui.table.drop.findDropLable(rowsField.drop,this.value);
                }
                othis.parent().find(ELEM_CELL+' p').html(
                    templet ? laytpl($(templet).html() || value).render(data) : value
                );
            } else {//输入框
                othis.parent().find(ELEM_CELL+' p').html(
                    templet ? laytpl($(templet).html() || this.value).render(data) : this.value
                );
            }
            othis.parent().data('content', this.value);
            othis.remove();
        });
        //Click on N_ode（Hide Expanders）
        that.elem.on('click','i.layui-tree-head', function(){
            var othis = $(this)
                ,index = othis.parents('tr').eq(0).data('index')
                ,options=that.config
                ,datas=table.getDataList(that.key);//Data
            var o=datas[index];
            that.treeNodeOpen(o,!o[table.config.cols.isOpen]);
            that.resize();
        });
        //Check Box Selection
        that.elem.on('click','input[name="'+TABLE_CHECKBOX_ID+'"]+', function(){
            var checkbox = $(this).prev()
                ,childs = that.layBody.find('input[name="'+TABLE_CHECKBOX_ID+'"]')
                ,index = checkbox.parents('tr').eq(0).data('index')
                ,checked = checkbox[0].checked
                ,obj=table.getDataList(that.config.id)[index]
                ,isAll = checkbox.attr('lay-filter') === 'layTableAllChoose';
            //Select All
            if(isAll){
                var list=table.getDataList(that.key);
                list.forEach(function (temo) {
                    if(!temo[table.config.cols.cheDisabled]){//可以选择的才设置
                        that.setCheckData(temo[table.config.indexName], checked);
                    }
                });
            } else {
                that.setCheckData(index, checked);
                if(options.isTree){
                    //处理下级
                    var sonList=that.treeFindSonData(obj);
                    sonList.forEach(function (temo) {
                        if(!temo[table.config.cols.cheDisabled]){//可以选择的才设置
                            that.setCheckData(temo[table.config.indexName], checked);
                        }
                    });

                    //处理上级
                    var temf=function (o) {
                        if(o==null)return;
                        if(o&&o.children.length>0){
                            var temis=true;
                            o.children.forEach(function (temo) {
                                if(temo[table.config.cols.isCheckName]){
                                    temis=false;
                                }
                            });
                            if(checked||temis){
                                that.setCheckData(o[table.config.indexName], checked);
                            }
                            var temuo=that.treeFindUpData(o);
                            if(temuo){
                                temf(temuo);
                            }
                        }
                    }
                    var uo=that.treeFindUpData(obj);
                    temf(uo);
                }
            }
            that.syncCheckAll();
            layui.event.call(this, MOD_NAME, 'checkbox('+ filter +')', {
                checked: checked
                ,data: table.getDataList(that.key) ? (obj || {}) : {}
                ,type: isAll ? 'all' : 'one'
            });
            typeof options.onCheck === 'function' && options.onCheck(obj,checked,isAll);
        });

        //Check box selection
        that.elem.on('click','input[name="'+TABLE_RADIO_ID+'"]+', function(){
            var checkbox = $(this).prev()
                ,index = checkbox.parents('tr').eq(0).data('index')
                ,obj=table.getDataList(that.config.id)[index];
            typeof options.onRadio === 'function' && options.onRadio(obj);
        });

        //Actions
        that.layBody.on('click', '*[lay-event]',function(){
            var othis = $(this)
                ,index = othis.parents('tr').eq(0).data('index')
                ,tr = that.layBody.find('tr[data-index="'+ index +'"]')
                ,ELEM_CLICK = 'layui-table-click'
                ,list = table.getDataList(that.key)
                ,data = table.getDataList(that.key)[index];
            layui.event.call(this, MOD_NAME, 'tool('+ filter +')', {
                data: data//table.clearCacheKey(data)
                ,event: othis.attr('lay-event')
                ,tr: tr
                ,del: function(){
                    table.delRow(options.id,data);
                }
                ,update: function(fields){
                    fields = fields || {};
                    layui.each(fields, function(key, value){
                        if(key in data){
                            var templet, td = tr.children('td[data-field="'+ key +'"]');
                            data[key] = value;
                            that.eachCols(function(i, item2){
                                if(item2.field == key && item2.templet){
                                    templet = item2.templet;
                                }
                            });
                            td.children(ELEM_CELL).html(
                                templet ? laytpl($(templet).html() || value).render(data) : value
                            );
                            td.data('content', value);
                        }
                    });
                }
            });
            tr.addClass(ELEM_CLICK).siblings('tr').removeClass(ELEM_CLICK);
        });
    }
    //Form overload
    thisTable.config = {};
    //Complete the puzzle automatically
    table.init();
    // layui.link('treeGrid.css');
    exports(MOD_NAME, table);
});