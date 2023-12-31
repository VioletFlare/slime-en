/**
 * < < None > >layui的tree重写
 * author: hsianglee
 * Recent modification time: 2019/01/07
 */

layui.define(["jquery","laytpl"], function (exports) {
    var $ = layui.jquery;
    var laytpl = layui.laytpl;
    var hint = layui.hint();

    var MOD_NAME="eleTree";
    
    //External Interface
    var eleTree={
        //Translate the following text to english: Event Monitoring
        on: function(events, callback){
            return layui.onevent.call(this, MOD_NAME, events, callback);
        },
        render: function(options) {
            var inst = new Class(options);
            return thisTree.call(inst);
        }
    }

    var thisTree=function() {
        var _self=this;
        var options = _self.config;

        // Leak Outside
        return {
            // Receive two arguments，1. 15th Last key 2. The array of node data
            updateKeyChildren: function(key,data) {
                if(options.data.length===0) return;
                return _self.updateKeyChildren.call(_self,key,data);
            },
            updateKeySelf: function(key,data) {
                if(options.data.length===0) return;
                return _self.updateKeySelf.call(_self,key,data);
            },
            remove: function(key) {
                if(options.data.length===0) return;
                return _self.remove.call(_self,key);
            },
            append: function(key,data) {
                if(options.data.length===0) return;
                return _self.append.call(_self,key,data);
            },
            insertBefore: function(key,data) {
                if(options.data.length===0) return;
                return _self.insertBefore.call(_self,key,data);
            },
            insertAfter: function(key,data) {
                if(options.data.length===0) return;
                return _self.insertAfter.call(_self,key,data);
            },
            // Receive two boolean Type of parameter，1. Is this a leaf node，Default Value Set false 2. Whether to include half-selected nodes，Default Value Set false
            getChecked: function(leafOnly, includeHalfChecked) {
                if(options.data.length===0) return;
                return _self.getChecked.call(_self,leafOnly, includeHalfChecked);
            },
            // The array of node data to receive
            setChecked: function(data) {
                if(options.data.length===0) return;
                return _self.setChecked.call(_self,data);
            },
            // 取消Select
            unCheckNodes: function() {
                if(options.data.length===0) return;
                return _self.unCheckNodes.call(_self);
            },
            expandAll: function() {
                options.elem.children(".eleTree-node").children(".eleTree-node-group").empty();
                _self.expandAll.call(_self,options.data,[],1,true);
                _self.unCheckNodes();
                _self.defaultChecked();
            },
            unExpandAll: function() {
                return _self.unExpandAll.call(_self);
            },
            reload: function(options) {
                return _self.reload.call(_self,options);
            },
            search: function(value) {
                return _self.search.call(_self,value);
            }
        }
    }

    // 2 Extra Bins
    var TPL_ELEM=function(options,floor,parentStatus) {
        return [
            '{{# for(var i=0;i<d.length;i++){ }}',
                '<div class="eleTree-node" data-'+options.request.key+'="{{d[i]["'+options.request.key+'"]}}" eletree-floor="'+floor+'" style="display: none;">',
                    '<div class="eleTree-node-content" style="padding-left: '+(options.indent*floor)+'px;">',
                        '<span class="eleTree-node-content-icon">',
                            '<i class="layui-icon layui-icon-triangle-r ',
                            function() {
                                if(options.lazy){
                                    var str=[
                                        '{{# if(!d[i]["'+options.request.isLeaf+'"]){ }}',
                                            'lazy-icon" ></i>',
                                        '{{# }else{ }}',
                                            'leaf-icon" style="color: transparent;" ></i>',
                                        '{{# } }}'
                                    ].join("");
                                    return str;
                                }
                                return ['{{# if(!d[i]["'+options.request.children+'"] || d[i]["'+options.request.children+'"].length===0){ }}',
                                        'leaf-icon" style="color: transparent;"',
                                    '{{# } }}',
                                    '"></i>'
                                ].join("");
                            }(),
                        '</span>',
                        function() {
                            if(options.showCheckbox){
                                var status="";
                                if(parentStatus==="1"){
                                    status='"1" checked';
                                }else if(parentStatus==="2"){
                                    status='"2"';
                                }else{
                                    status='"0"';
                                }
                                return [
                                    '{{# if(d[i]["'+options.request.checked+'"]) { }}',
                                        '<input type="checkbox" name="eleTree-node" eleTree-status="1" checked class="eleTree-hideen ',
                                    '{{# }else{ }}',
                                        '<input type="checkbox" name="eleTree-node" eleTree-status='+status+' class="eleTree-hideen ',
                                    '{{# } }}',

                                    '{{# if(d[i]["'+options.request.disabled+'"]) { }}',
                                        'eleTree-disabled',
                                    '{{# } }}',
                                    '" />'
                                ].join("");
                            }
                            return ''
                        }(),
                        '<span class="eleTree-node-content-label">{{d[i]["'+options.request.name+'"]}}</span>',
                    '</div>',
                    '<div class="eleTree-node-group">',
                    '</div>',
                '</div>',
            '{{# } }}'
        ].join("");
    }

    var TPL_NoText=function() {
        return '<h3 class="eleTree-noText" style="text-align: center;height: 30px;line-height: 30px;color: #888;">{{d.emptText}}</h3>';
    }

    var Class=function(options) {
        options.response=$.extend({}, this.config.response, options.response);
        options.request=$.extend({}, this.config.request, options.request);
        this.config = $.extend({}, this.config, options);
        this.prevClickEle=null;
        this.addKeyIndex=20181201;
        this.nameIndex=1;
        this.render();
    };

    Class.prototype={
        constructor: Class,
        config: {
            elem: "",
            data: [],
            emptText: "Data Engine",        // Text to show in the assistant when no description is given.
            renderAfterExpand: true,    // Whether to expand a node before painting its children
            highlightCurrent: false,    // Whether to highlight the current node，Default value is false。
            defaultExpandAll: false,    // Expand all nodes by default
            expandOnClickNode: true,    // Whether to expand or contract nodes when clicking on them， Default Value Set true，If you don't know the answer to a question, please don't share false information. false，Then expand or collapse nodes only when there are some arrow icons.。
            checkOnClickNode: false,    // Whether to select the node when clicking on it，Default Value Set false，AnswerOnly when the checkbox is clickedSelect15th Last。
            defaultExpandedKeys: [],    // Default Expanded Nodes key The following text is a sample answer to the question "What is your name?."
            autoExpandParent: true,     // Whether to expand the child node when the child node is being expanded
            showCheckbox: false,        // Whether the node is selectable
            checkStrictly: false,       // In the case of a check box，Whether to strictly follow the father-son non-relation rule，Default to false
            defaultCheckedKeys: [],     // Default node to use in the future key The following text is a sample answer to the question "What is your name?."
            accordion: false,           // Whether to expand the child's menu only when the child's menu is empty（Effect:）
            indent: 16,                 // Vertical space between two consecutive rows，The unit of the current calculation
            lazy: false,                // Whether to lazy-load child nodes，with load Combination method
            load: function() {},        // Loading child data，Only when lazy Property:true When to take effect
            draggable: false,           // Is the drag and drop function enabled
            contextmenuList: [],        // Enable Right Menubar，Supported operations are："copy","add","edit","remove"
            searchNodeMethod: null,     // What method to use when sorting tree nodes，Back true 表示这个节点可以显示，Back false Then hide this node

            method: "get",
            url: "",
            contentType: "",
            headers: {},
            done: null,
            
            response: {
                statusName: "code",
                statusCode: 0,
                dataName: "data"
            },
            request: {
                name: "label",
                key: "id",
                children: "children",
                disabled: "disabled",
                checked: "checked",
                isLeaf: "isLeaf"
            }
        },
        render: function() {
            if(this.config.indent>30){
                this.config.indent=30;
            }else if(this.config.indent<10){
                this.config.indent=10;
            }
            var options=this.config;
            options.where=options.where || {};
            if(!options.elem) return hint.error("MissingelemParameters");
            options.elem=typeof options.elem === "string" ? $(options.elem) : options.elem;
            this.filter=options.elem.attr("lay-filter");
            // loadLoading Frame
            options.elem.append('<div class="eleTree-loadData"><i class="layui-icon layui-icon-loading layui-icon layui-anim layui-anim-rotate layui-anim-loop"></i></div>')
            
            // Load Actions
            if(options.data.length===0){
                this.ajaxGetData();
            }else{
                this.renderData();
            }
        },
        renderData: function() {
            var options=this.config;
            // Translate the following text to english
            laytpl(TPL_ELEM(options,0)).render(options.data, function(string){
                options.elem.html(string).children().show();
            }); 
            // 懒 Load > Expand all > Expand Item > Show all subnodes > Human > Only paint on this layer（Default）
            // 判断所有domWhether to load all
            if(!options.lazy){
                if(!options.renderAfterExpand || options.defaultExpandAll || options.defaultExpandedKeys.length>0 || options.defaultCheckedKeys.length>0){
                    this.expandAll(options.data,[],1);
                }
            }

            this.eleTreeEvent();
            this.checkboxRender();
            this.checkboxEvent();
            this.defaultChecked();
            this.nodeEvent();
            this.rightClickMenu();
            if(!options.checkStrictly){
                this.checkboxInit();
            }
        },
        ajaxGetData: function() {
            var options=this.config;
            var _self=this;
            if(!options.url) {
                laytpl(TPL_NoText()).render(options, function(string){
                    options.elem.html(string);
                }); 
                return;
            }
            var data = $.extend({}, options.where);
            if(options.contentType && options.contentType.indexOf("application/json") == 0){ //Submit json Text Format
              data = JSON.stringify(data);
            }

            $.ajax({
                type: options.method || 'get'
                ,url: options.url
                ,contentType: options.contentType
                ,data: data
                ,dataType: 'json'
                ,headers: options.headers || {}
                ,success: function(res){
                    if(res[options.response.statusName] != options.response.statusCode || !res[options.response.dataName]){
                        hint.error("Please check that the data format conforms to the standard");
                        typeof options.done === 'function' && options.done(res);
                        return;
                    }
                    options.data=res[options.response.dataName];
                    _self.renderData();
                    typeof options.done === 'function' && options.done(res);
                }
            });
        },
        reload: function(options) {
            var _self=this;
            if(this.config.data && this.config.data.constructor === Array) this.config.data=[];
            this.config = $.extend({}, this.config, options);
            $(this.config.elem).off();  // The following sources are available: %s，Prevent multiple binding events
            // reloadRecord selected data
            // this.getChecked().forEach(function(val) {
            //     if($.inArray(val.key,this.config.defaultCheckedKeys)===-1){
            //         this.config.defaultCheckedKeys.push(val.key);
            //     }
            // },this);
            return eleTree.render(this.config)
        },
        // Pull Down
        eleTreeEvent: function() {
            var _self=this;
            var options=this.config;
            // Pull Down
            var expandOnClickNode=options.expandOnClickNode?".eleTree-node-content":".eleTree-node-content>.eleTree-node-content-icon";
            options.elem.on("click",expandOnClickNode,function(e) {
                e.stopPropagation();
                var eleTreeNodeContent=$(this).parent(".eleTree-node").length===0?$(this).parent(".eleTree-node-content"):$(this);
                var eleNode=eleTreeNodeContent.parent(".eleTree-node");
                var sibNode=eleTreeNodeContent.siblings(".eleTree-node-group");
                var el=eleTreeNodeContent.children(".eleTree-node-content-icon").children(".layui-icon");

                // AddactiveBackground
                if(_self.prevClickEle) _self.prevClickEle.removeClass("eleTree-node-content-active");
                if(options.highlightCurrent) eleTreeNodeContent.addClass("eleTree-node-content-active");
                _self.prevClickEle=eleTreeNodeContent;

                

                if(el.hasClass("icon-rotate")){
                    // Merge
                    sibNode.children(".eleTree-node:not(.eleTree-search-hide)").hide("fast");
                    el.removeClass("icon-rotate");
                    return;
                }

                if(sibNode.children(".eleTree-node").length===0){
                    var floor=Number(eleNode.attr("eletree-floor"))+1;

                    var data=_self.reInitData(eleNode);
                    var d=data.currentData;
                    // Whether to lazy load
                    if(options.lazy && el.hasClass("lazy-icon")){
                        el.removeClass("layui-icon-triangle-r").addClass("layui-icon-loading layui-anim layui-anim-rotate layui-anim-loop");
                        options.load(d,function(getData) {
                            d[options.request.children]=getData;
                            var eletreeStatus=eleTreeNodeContent.children("input.eleTree-hideen").attr("eletree-status");
                            if(d[options.request.children] && d[options.request.children].length>0){
                                laytpl(TPL_ELEM(options,floor,eletreeStatus)).render(d[options.request.children], function(string){
                                    sibNode.append(string).children().show("fast");
                                });
                            }else{
                                el.css("color","transparent").addClass("leaf-icon");
                            }
                            el.removeClass("lazy-icon layui-icon-loading layui-anim layui-anim-rotate layui-anim-loop").addClass("layui-icon-triangle-r icon-rotate");
                            _self.checkboxRender();

                            // 10th Last（Please wait while the assistant is working.）
                        })
                    }else{
                        var eletreeStatus=eleTreeNodeContent.children("input.eleTree-hideen").attr("eletree-status");
                        d[options.request.children] && d[options.request.children].length>0 && laytpl(TPL_ELEM(options,floor,eletreeStatus)).render(d[options.request.children], function(string){
                            sibNode.append(string);
                        });

                        // Select Grandfather
                        var eleNode1=sibNode.children(".eleTree-node").eq(0);
                        if(eleNode1.length===0){
                            _self.checkboxRender();
                            return;
                        }
                        var siblingNode1=eleNode1.siblings(".eleTree-node");
                        var item1=eleNode1.children(".eleTree-node-content").children(".eleTree-hideen").get(0);
                        _self.selectParents(item1,eleNode1,siblingNode1);
                        _self.checkboxRender();
                    }
                }
                // If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.
                sibNode.children(".eleTree-node:not(.eleTree-search-hide)").show("fast");
                el.addClass("icon-rotate");
                // Effect:
                if(options.accordion){
                    var node=eleTreeNodeContent.parent(".eleTree-node").siblings(".eleTree-node");
                    node.children(".eleTree-node-group").children(".eleTree-node:not(.eleTree-search-hide)").hide("fast");
                    node.children(".eleTree-node-content").children(".eleTree-node-content-icon").children(".layui-icon").removeClass("icon-rotate");
                }
            })
        },
        // checkboxSelect
        checkboxEvent: function() {
            var options=this.config;
            var _self=this;
            var checkOnClickNode=options.checkOnClickNode?".eleTree-node-content":".eleTree-checkbox";
            // inputAdd AttributeseleTree-status：AnswerinputThree states of matter，"0":YesSelect，"1":Select，"2":Select the children to be picked up
            options.elem.on("click",checkOnClickNode,function(e,type) {
                e.stopPropagation();
                var eleTreeNodeContent=$(this).parent(".eleTree-node").length===0?$(this).parent(".eleTree-node-content"):$(this);
                var checkbox=eleTreeNodeContent.children(".eleTree-checkbox");
                if(checkbox.hasClass("eleTree-checkbox-disabled")) return;
                // Get the click data
                var node=eleTreeNodeContent.parent(".eleTree-node");
                // var d=_self.reInitData(node).currentData;
                // Actuallyinput
                var inp=checkbox.siblings(".eleTree-hideen").get(0);
                var childNode=eleTreeNodeContent.siblings(".eleTree-node-group").find("input[name='eleTree-node']");

                // AddactiveBackground
                if(_self.prevClickEle) _self.prevClickEle.removeClass("eleTree-node-content-active");
                if(options.highlightCurrent) eleTreeNodeContent.addClass("eleTree-node-content-active");
                _self.prevClickEle=eleTreeNodeContent;
                
                if(!inp){
                    return;
                }

                if(inp.checked){
                    // 反选自身
                    $(inp).prop("checked",false).attr("eleTree-status","0");
                    // Click on Grandfather generation select Great Grandson generation
                    if(!options.checkStrictly){
                        childNode.prop("checked",false);
                        childNode.attr("eleTree-status","0");
                    }
                    
                }else{
                    // 反选自身
                    $(inp).prop("checked",true).attr("eleTree-status","1");
                    // Click on Grandfather generation select Great Grandson generation
                    if(!options.checkStrictly){
                        childNode.prop("checked",true).attr("eleTree-status","1");
                    }
                }

                var eleNode=eleTreeNodeContent.parent(".eleTree-node");
                // Click on Grandfather Layer(递归)
                if(!options.checkStrictly){
                    var siblingNode=eleNode.siblings(".eleTree-node");
                    // Click on Grandfather Layer(递归)
                    _self.selectParents(inp,eleNode,siblingNode);
                }
                
                _self.checkboxRender();

                if(type==="default") return;
                layui.event.call(inp, MOD_NAME, 'nodeChecked('+ _self.filter +')', {
                    node: eleNode,
                    data: _self.reInitData(eleNode),
                    isChecked: inp.checked
                });
            })
        },
        // Have fun with your new device! checked:true 的默认选中项渲染父子层
        checkboxInit: function() {
            var options=this.config;
            var _self=this;
            options.elem.find("input[eleTree-status='1']").each(function(index,item) {
                var checkboxEl=$(item).siblings(".eleTree-checkbox");
                var childNode=checkboxEl.parent(".eleTree-node-content").siblings(".eleTree-node-group").find("input[name='eleTree-node']");
                // Select current
                checkboxEl.addClass("eleTree-checkbox-checked");
                checkboxEl.children("i").addClass("layui-icon-ok").removeClass("eleTree-checkbox-line");
                // Select a child
                childNode.prop("checked","checked").attr("eleTree-status","1");
                childNode.siblings(".eleTree-checkbox").addClass("eleTree-checkbox-checked");
                childNode.siblings(".eleTree-checkbox").children("i").addClass("layui-icon-ok").removeClass("eleTree-checkbox-line");
                
                // Select Grandfather
                var eleNode=checkboxEl.parent(".eleTree-node-content").parent(".eleTree-node");
                var siblingNode=eleNode.siblings(".eleTree-node");
                _self.selectParents(item,eleNode,siblingNode);
            })
            _self.checkboxRender();
        },
        // Select the parent of the currently selected element
        selectParents: function(inp,eleNode,siblingNode) {
            // inp: Actuallyinput(dom元素)
            // eleNode: inputClass parent（.eleTree-node）
            // siblingNode: Child LayerSame suitAnswer: Brother
            while (Number(eleNode.attr("eletree-floor"))!==0) {
                // Same suitinputSave Status to Array
                var arr=[];
                arr.push($(inp).attr("eleTree-status"));
                siblingNode.each(function(index,item) {
                    var siblingIsChecked=$(item).children(".eleTree-node-content").children("input[name='eleTree-node']").attr("eleTree-status");
                    arr.push(siblingIsChecked);
                })
                // The actual value of the parent elementinput
                var parentInput=eleNode.parent(".eleTree-node-group").siblings(".eleTree-node-content").children("input[name='eleTree-node']");
                // The following sources are available:checkbox替代
                var parentCheckbox=parentInput.siblings(".eleTree-checkbox");
                // If checked, the parent will be selected when the child is selected
                if(arr.every(function(val) {
                    return val==="1";
                })){
                    parentInput.prop("checked",true).attr("eleTree-status","1");
                }
                // If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.checkbox第三种状态
                if(arr.some(function(val) {
                    return val==="0" || val==="2";
                })){
                    parentInput.attr("eleTree-status","2");
                }
                // Unselect all tracks(And remove the third state)
                if(arr.every(function(val) {
                    return val==="0";
                })){
                    parentInput.prop("checked",false);
                    parentInput.attr("eleTree-status","0");
                }

                var parentNode=eleNode.parents("[eletree-floor='"+(Number(eleNode.attr("eletree-floor"))-1)+"']");
                var parentCheckbox=parentNode.children(".eleTree-node-content").children("input[name='eleTree-node']").get(0);
                var parentSiblingNode=parentNode.siblings(".eleTree-node");
                eleNode=parentNode;
                inp=parentCheckbox;
                siblingNode=parentSiblingNode;
            }
        },
        // Expand all
        expandAll: function(data,arr,floor,isMethodsExpandAll) {
            var options=this.config;
            var _self=this;
            data.forEach(function(val,index) {
                arr.push(index);
                if(val[options.request.children] && val[options.request.children].length>0){
                    var el=options.elem.children(".eleTree-node").eq(arr[0]).children(".eleTree-node-group");
                    for(var i=1;i<arr.length;i++){
                        el=el.children(".eleTree-node").eq(arr[i]).children(".eleTree-node-group");
                    }
                    laytpl(TPL_ELEM(options,floor)).render(val[options.request.children], function(string){
                        el.append(string);
                        // Allows the user to decide whether to expand all
                        if(options.defaultExpandAll || isMethodsExpandAll){
                            el.siblings(".eleTree-node-content").children(".eleTree-node-content-icon").children(".layui-icon").addClass("icon-rotate");
                            el.children().show();
                        }else if(options.defaultExpandedKeys.length>0) {
                            // Expandid项
                            var id=el.parent(".eleTree-node").attr("data-"+options.request.key);
                            id=isNaN(id) ? id : Number(id);
                            if($.inArray(id,options.defaultExpandedKeys)!==-1){
                                el.siblings(".eleTree-node-content").children(".eleTree-node-content-icon").children(".layui-icon").addClass("icon-rotate");
                                el.children().show();
                                // Expand child items of an enclosing group
                                if(options.autoExpandParent){
                                    var eleP=el.parent(".eleTree-node[data-"+options.request.key+"]").parents(".eleTree-node");
                                    eleP.each(function(i,item) {
                                        if($(item).attr("data-"+options.request.key)){
                                            $(item).children(".eleTree-node-group").siblings(".eleTree-node-content").children(".eleTree-node-content-icon").children(".layui-icon").addClass("icon-rotate");
                                            $(item).children(".eleTree-node-group").children().show();
                                        }
                                    })
                                }
                            }
                        }
                    });
                    floor++;
                    _self.expandAll(val[options.request.children],arr,floor,isMethodsExpandAll);
                    floor--;
                }
                // Reset array index
                arr.pop();
            })

            
        },
        // Default to English
        defaultChecked: function() {
            var options=this.config;
            if(options.defaultCheckedKeys.length===0){
                return false;
            }
            // 判断是否父子无关
            if(options.checkStrictly){
                options.defaultCheckedKeys.forEach(function(val,index) {
                    var nodeContent=options.elem.find("[data-"+options.request.key+"='"+val+"']").children(".eleTree-node-content");
                    // Please select a product
                    if(nodeContent.children(".eleTree-hideen").prop("checked")===false){
                        nodeContent.children(".eleTree-checkbox").trigger("click",["default"]);
                    }
                })
                return false;
            }
            // Prefer Plain Text
            var arr=$.extend([],options.defaultCheckedKeys);
            options.defaultCheckedKeys.forEach(function(val,index) {
                options.elem.find("[data-"+options.request.key+"='"+val+"']").find("[data-"+options.request.key+"]").each(function(i,item) {
                    var id=$(item).attr("data-"+options.request.key);
                    id=isNaN(id) ? id : Number(id);
                    var isInArrayIndex=$.inArray(id,arr);
                    if(isInArrayIndex!==-1){
                        arr.splice(isInArrayIndex,1);
                    }
                })
            })
            arr.forEach(function(val,index) {
                var nodeContent=options.elem.find("[data-"+options.request.key+"='"+val+"']").children(".eleTree-node-content");
                // Please select a product
                if(nodeContent.children(".eleTree-hideen").prop("checked")===false){
                    nodeContent.children(".eleTree-checkbox").trigger("click",["default"]);
                }
            })
        },
        // 自定义checkboxAnalyse
        checkboxRender: function() {
            var options=this.config;
            options.elem.find(".eleTree-checkbox").remove();
            options.elem.find("input.eleTree-hideen[type=checkbox]").each(function(index,item){
                if($(item).hasClass("eleTree-disabled")){
                    $(item).after('<div class="eleTree-checkbox eleTree-checkbox-disabled"><i class="layui-icon"></i></div>');
                }else{
                    $(item).after('<div class="eleTree-checkbox"><i class="layui-icon"></i></div>');
                }

                var checkbox=$(item).siblings(".eleTree-checkbox");
                if($(item).attr("eletree-status")==="1"){
                    checkbox.addClass("eleTree-checkbox-checked");
                    checkbox.children("i").addClass("layui-icon-ok").removeClass("eleTree-checkbox-line");
                }else if($(item).attr("eletree-status")==="0"){
                    checkbox.removeClass("eleTree-checkbox-checked");
                    checkbox.children("i").removeClass("layui-icon-ok eleTree-checkbox-line");
                }else if($(item).attr("eletree-status")==="2"){
                    checkbox.addClass("eleTree-checkbox-checked");
                    checkbox.children("i").removeClass("layui-icon-ok").addClass("eleTree-checkbox-line");
                }
                
            })
        },
        // AnswerdomFind corresponding data in the nodes
        reInitData: function(node) {
            var options=this.config;
            var i=node.index();
            var floor=Number(node.attr("eletree-floor"));
            var arr=[];     // Corresponding nodeindex
            while (floor>=0) {
                arr.push(i);
                floor=floor-1;
                node=node.parents("[eletree-floor='"+floor+"']");
                i=node.index();
            }
            arr=arr.reverse();
            var oData=this.config.data;
            // Parent node data of the current node
            var parentData=oData[arr[0]];
            // Current node'sdataData
            var d = oData[arr[0]];
            for(var i = 1; i<arr.length; i++){
                d = d[options.request.children]?d[options.request.children][arr[i]]:d;
            }
            for(var i = 1; i<arr.length-1; i++){
                parentData = parentData[options.request.children]?parentData[options.request.children][arr[i]]:parentData;
            }

            return {
                currentData: d,
                parentData: {
                    data: parentData,
                    childIndex: arr[arr.length-1]
                },
                index: arr
            }
        },
        // AnswerkeyFind Data
        keySearchToOpera: function(key,callback) {
            var options=this.config;
            var _self=this;
            // Find Data
            var fn=function(data) {
                var obj={
                    i: 0,
                    len: data.length
                }
                for(;obj.i<obj.len;obj.i++){
                    if(data[obj.i][options.request.key]!==key){
                        if(data[obj.i][options.request.children] && data[obj.i][options.request.children].length>0){
                            fn(data[obj.i][options.request.children]);
                        }
                    }else{
                        callback(data,obj);
                    }
                }
            }
            fn(options.data);
        },
        updateKeyChildren: function(key,data) {
            var options=this.config;
            var node=options.elem.find("[data-"+options.request.key+"='"+key+"']");
            var floor=Number(node.attr("eletree-floor"))+1;
            var _self=this;
            
            this.keySearchToOpera(key,function(d,obj) {
                // Data Update
                d[obj.i][options.request.children]=data;
                // domUpdated
                node.length!==0 && laytpl(TPL_ELEM(options,floor)).render(data, function(string){
                    $(node).children(".eleTree-node-group").empty().append(string);
                    options.defaultExpandAll && $(node).children(".eleTree-node-group").children().show();
                }); 
                _self.unCheckNodes();
                _self.defaultChecked();
            });
        },
        updateKeySelf: function(key,data) {
            var options=this.config;
            var node=options.elem.find("[data-"+options.request.key+"='"+key+"']").children(".eleTree-node-content");
            var floor=Number(node.attr("eletree-floor"))+1;
            data[options.request.name] && node.children(".eleTree-node-content-label").text(data[options.request.name]);
            data[options.request.disabled] && node.children(".eleTree-hideen").addClass("eleTree-disabled")
                .siblings(".eleTree-checkbox").addClass("eleTree-checkbox-disabled");
            // Data Update
            var getData=this.keySearchToOpera(key,function(d,obj) {
                data[options.request.key]=d[obj.i][options.request.key];
                data[options.request.children]=d[obj.i][options.request.children];
                d[obj.i]=$.extend({},d[obj.i],data);
                console.log(options.data);
            });
        },
        remove: function(key) {
            var options=this.config;
            var node=options.elem.find("[data-"+options.request.key+"='"+key+"']");
            var pElem=node.parent(".eleTree-node-group");
            // Data Delete
            this.keySearchToOpera(key,function(data,obj) {
                data.splice(obj.i,1);
                obj.i--;
                obj.len--;

                node.length!==0 && options.elem.find("[data-"+options.request.key+"='"+key+"']").remove();
                if(pElem.children(".eleTree-node").length===0){
                    pElem.siblings(".eleTree-node-content").children(".eleTree-node-content-icon").children(".layui-icon").css("color", "transparent");
                }
            });
            this.unCheckNodes();
            this.defaultChecked();
        },
        append: function(key,data) {
            var options=this.config;
            var node=options.elem.find("[data-"+options.request.key+"='"+key+"']");
            var floor=Number(node.attr("eletree-floor"))+1;
            // Data Update
            this.keySearchToOpera(key,function(d,obj) {
                if(d[obj.i][options.request.children]){
                    d[obj.i][options.request.children].push(data);
                }else{
                    d[obj.i][options.request.children]=[data];
                }
                var arr=d[obj.i][options.request.children];
                // Add Length1，Then there are no triangles，Add Triangle
                if(arr.length===1){
                    node.children(".eleTree-node-content").find(".eleTree-node-content-icon .layui-icon").removeAttr("style").addClass("icon-rotate");
                }
                var len=arr.length;
                var eletreeStatus=node.children(".eleTree-node-content").children("input.eleTree-hideen").attr("eletree-status");
                eletreeStatus=eletreeStatus==="2" ? "0" : eletreeStatus;
                node.length!==0 && laytpl(TPL_ELEM(options,floor,eletreeStatus)).render([arr[len-1]], function(string){
                    node.children(".eleTree-node-group").append(string).children().show();
                }); 
            });
            this.checkboxRender();
        },
        insertBefore: function(key,data) {
            var options=this.config;
            var node=options.elem.find("[data-"+options.request.key+"='"+key+"']");
            var floor=Number(node.attr("eletree-floor"));
            // Data Update
            this.keySearchToOpera(key,function(d,obj) {
                d.splice(obj.i,0,data);
                obj.i++;
                obj.len++;
                var eletreeStatus=node.parent(".eleTree-node-group").length===0 ? "0" : node.parent(".eleTree-node-group").parent(".eleTree-node")
                .children(".eleTree-node-content").children("input.eleTree-hideen").attr("eletree-status");
                eletreeStatus=eletreeStatus==="2" ? "0" : eletreeStatus;
                node.length!==0 && laytpl(TPL_ELEM(options,floor,eletreeStatus)).render([data], function(string){
                    node.before(string).prev(".eleTree-node").show();
                }); 
            });
            this.checkboxRender();
        },
        insertAfter: function(key,data) {
            var options=this.config;
            var node=options.elem.find("[data-"+options.request.key+"='"+key+"']");
            var floor=Number(node.attr("eletree-floor"));
            // Data Update
            this.keySearchToOpera(key,function(d,obj) {
                d.splice(obj.i+1,0,data);
                obj.i++;
                obj.len++;
                var eletreeStatus=node.parent(".eleTree-node-group").length===0 ? "0" : node.parent(".eleTree-node-group").parent(".eleTree-node")
                .children(".eleTree-node-content").children("input.eleTree-hideen").attr("eletree-status");
                eletreeStatus=eletreeStatus==="2" ? "0" : eletreeStatus;
                node.length!==0 && laytpl(TPL_ELEM(options,floor,eletreeStatus)).render([data], function(string){
                    $(node).after(string).next(".eleTree-node").show();
                }); 
            });
            this.checkboxRender();
            // if(!options.lazy){
            //     if(!options.renderAfterExpand || options.defaultExpandAll || options.defaultExpandedKeys.length>0){
            //         this.expandAll(options.data,[],1);
            //     }
            // }
        },
        getChecked: function(leafOnly, includeHalfChecked) {
            var options=this.config
                ,el
                ,arr=[];
            leafOnly=leafOnly || false;
            includeHalfChecked=includeHalfChecked || false;
            if(leafOnly){
                el=options.elem.find(".layui-icon.leaf-icon").parent(".eleTree-node-content-icon")
                    .siblings("input.eleTree-hideen[eletree-status='1']");
            }else if(includeHalfChecked){
                el=options.elem.find("input.eleTree-hideen[eletree-status='1'],input.eleTree-hideen[eletree-status='2']");
            }else{
                el=options.elem.find("input.eleTree-hideen[eletree-status='1']");
            }
            el.each(function(index,item) {
                var obj={};
                var id=$(item).parent(".eleTree-node-content").parent(".eleTree-node").attr("data-"+options.request.key);
                id=isNaN(id) ? id : Number(id);
                obj[options.request.key]=id;
                obj.elem=item;
                obj.othis=$(item).siblings(".eleTree-checkbox").get(0)
                arr.push(obj);
            })
            return arr;
        },
        setChecked: function(arr) {
            var options=this.config;
            this.unCheckNodes();
            arr.forEach(function(val) {
                if($.inArray(val,options.defaultCheckedKeys)===-1){
                    options.defaultCheckedKeys.push(val);
                }
            })
            this.defaultChecked();
        },
        unCheckNodes: function() {
            var options=this.config;
            options.elem.find("input.eleTree-hideen[eletree-status='1'],input.eleTree-hideen[eletree-status='2']").each(function(index,item) {
                $(item).attr("eletree-status","0").prop("checked",false);
            });
            this.checkboxRender();
        },
        unExpandAll: function() {
            var options=this.config;
            options.elem.find(".layui-icon.icon-rotate").removeClass("icon-rotate")
                .parent(".eleTree-node-content-icon").parent(".eleTree-node-content")
                .siblings(".eleTree-node-group").children(".eleTree-node").hide();
        },
        // Events
        nodeEvent: function() {
            var _self=this;
            var options=this.config;
            // Callback to be issued when a node is clicked
            options.elem.on("click",".eleTree-node-content",function(e) {
                var eleNode=$(this).parent(".eleTree-node");
                $("#tree-menu").hide().remove();
                layui.event.call(eleNode, MOD_NAME, 'nodeClick('+ _self.filter +')', {
                    node: eleNode,
                    data: _self.reInitData(eleNode),
                    event: e
                });
            })
            // Callback Events
            options.elem.on("contextmenu",".eleTree-node-content",function(e) {
                var eleNode=$(this).parent(".eleTree-node");
                layui.event.call(eleNode, MOD_NAME, 'nodeContextmenu('+ _self.filter +')', {
                    node: eleNode,
                    data: _self.reInitData(eleNode),
                    event: e
                });
            })
            // Callback events when a node is dragged
            options.draggable && options.elem.on("mousedown",".eleTree-node-content",function(e) {
                var time=0;
                var eleNode=$(this).parent(".eleTree-node");
                var eleFloor=Number(eleNode.attr("eletree-floor"));
                var groupNode=eleNode.parent(".eleTree-node-group");

                e.stopPropagation();
                options.elem.css("user-select","none");
                var cloneNode=eleNode.clone(true);
                var temNode=eleNode.clone(true);

                var x=e.clientX-options.elem.offset().left;
                var y=e.clientY-options.elem.offset().top;
                options.elem.append(cloneNode);
                cloneNode.css({
                    "display": "none",
                    "opacity": 0.7,
                    "position": "absolute",
                    "background-color": "#f5f5f5",
                    "width": "100%"
                })

                var currentData=_self.reInitData(eleNode);

                var isStop=false;

                $(document).on("mousemove",function(e) {
                    // tFor distinctionclickEvents
                    time++;
                    if(time>2){
                        var xx=e.clientX-options.elem.offset().left+10;
                        var yy=e.clientY-options.elem.offset().top+$(document).scrollTop()-5;   // Add a browser scroll height

                        cloneNode.css({
                            display: "block",
                            left: xx+"px",
                            top: yy+"px"
                        })
                    }
                }).on("mouseup",function(e) {
                    $(document).off("mousemove").off("mouseup");
                    var target=$(e.target).parents(".eleTree-node").eq(0);
                    cloneNode.remove();
                    options.elem.css("user-select","auto");

                    
                    // 当前点击的是否时最外层
                    var isCurrentOuterMost=eleNode.parent().get(0).isEqualNode(options.elem.get(0))
                    // Is the target the outermost layer
                    var isTargetOuterMost=$(e.target).get(0).isEqualNode(options.elem.get(0))
                    if(isTargetOuterMost){
                        target=options.elem;
                    }
                    // Determine if it's outside the boundary
                    if(target.parents(options.elem).length===0 && !isTargetOuterMost){
                        return;
                    }
                    // Is the start and end node the same?
                    if(target.get(0).isEqualNode(eleNode.get(0))){
                        return;
                    }
                    // Is the parent node a child node
                    var tFloor=target.attr("eletree-floor");
                    var isInChild=false;
                    eleNode.find("[eletree-floor='"+tFloor+"']").each(function() {
                        if(this.isEqualNode(target.get(0))){
                            isInChild=true;
                        }
                    })
                    if(isInChild){
                        return;
                    }

                    var targetData=_self.reInitData(target);
                    layui.event.call(target, MOD_NAME, 'nodeDrag('+ _self.filter +')', {
                        current: {
                            node: eleNode,
                            data: currentData
                        },
                        target: {
                            node: target,
                            data: targetData
                        },
                        stop: function() {
                            isStop=true;
                        }
                    });
                    // If you don't know the answer to a question, please don't share false information.
                    if(isStop){
                        return false;
                    }

                    // Data changed
                    var currList=currentData.parentData.data[options.request.children]
                    var currIndex=currentData.parentData.childIndex
                    var currData=currentData.currentData;
                    var tarData=targetData.currentData;
                    // Is this the outermost layer
                    isCurrentOuterMost ? options.data.splice(currIndex,1) : currList.splice(currIndex,1)
                    // Is the target the outermost layer
                    isTargetOuterMost ? options.data.push(currData) : (function() {
                        !tarData[options.request.children] ? tarData[options.request.children]=[] : "";
                        tarData[options.request.children].push(currData);
                    })()

                    // dom Swap
                    eleNode.remove();
                    // Top-Level Decision
                    if(isTargetOuterMost){
                        target.append(temNode);
                        var floor=0;
                    }else{
                        target.children(".eleTree-node-group").append(temNode);
                        var floor=Number(target.attr("eletree-floor"))+1;
                    }
                    // 加floor和padding
                    temNode.attr("eletree-floor",String(floor));
                    temNode.children(".eleTree-node-content").css("padding-left",floor*options.indent+"px");
                    // AnswerfloorValue of the progress barfloor
                    var countFloor=eleFloor-floor;
                    temNode.find(".eleTree-node").each(function(index,item) {
                        var f=Number($(item).attr("eletree-floor"))-countFloor;
                        $(item).attr("eletree-floor",String(f));
                        $(item).children(".eleTree-node-content").css("padding-left",f*options.indent+"px");
                    })
                    // 原domGo to the Triangle
                    var leaf=groupNode.children(".eleTree-node").length===0;
                        leaf && groupNode.siblings(".eleTree-node-content")
                        .children(".eleTree-node-content-icon").children(".layui-icon")
                        .removeClass("icon-rotate").css("color","transparent");
                    // Currently selected text in editor
                    var cLeaf=target.children(".eleTree-node-group").children(".eleTree-node").length===0;
                        !cLeaf && target.children(".eleTree-node-content")
                        .children(".eleTree-node-content-icon").children(".layui-icon")
                        .addClass("icon-rotate").removeAttr("style");

                    _self.unCheckNodes();
                    _self.defaultChecked();

                })
            })
        },
        rightClickMenu: function() {
            var _self=this;
            var options=this.config;
            if(options.contextmenuList.length<=0){
                return;
            }
            $(document).on("click",function() {
                $("#tree-menu").hide().remove();
            });
            var menuStr=['<ul id="tree-menu">'
                ,$.inArray("copy",options.contextmenuList)!==-1?'<li class="copy"><a href="javascript:;">Copy</a></li>':''
                ,$.inArray("add",options.contextmenuList)!==-1?'<li class="add"><a href="javascript:;">Add</a></li>'+
                    '<li class="insertBefore"><a href="javascript:;">Before Insert Polyline</a></li>'+
                    '<li class="insertAfter"><a href="javascript:;">After Inserting a node</a></li>'+
                    '<li class="append"><a href="javascript:;">Inserting child node</a></li>' : ""
                ,$.inArray("edit",options.contextmenuList)!==-1?'<li class="edit"><a href="javascript:;">Edit</a></li>':''
                ,$.inArray("remove",options.contextmenuList)!==-1?'<li class="remove"><a href="javascript:;">12</a></li>':''
            ,'</ul>'].join("");
            this.treeMenu=$(menuStr);
            options.elem.off("contextmenu").on("contextmenu",".eleTree-node-content",function(e) {
                var that=this;
                e.stopPropagation();
                e.preventDefault();
                // AddactiveBackground
                if(_self.prevClickEle) _self.prevClickEle.removeClass("eleTree-node-content-active");
                $(this).addClass("eleTree-node-content-active");
                var eleNode=$(this).parent(".eleTree-node");
                var nodeData=_self.reInitData(eleNode);

                // Menu position
                $(document.body).after(_self.treeMenu);
                $("#tree-menu li.insertBefore,#tree-menu li.insertAfter,#tree-menu li.append").hide();
                $("#tree-menu li.copy,#tree-menu li.add,#tree-menu li.edit,#tree-menu li.remove").show();
                $("#tree-menu").css({
                    left: e.pageX,
                    top: e.pageY
                }).show();
                // Copy
                $("#tree-menu li.copy").off().on("click",function() {
                    var el = $(that).children(".eleTree-node-content-label").get(0);
                    var selection = window.getSelection();
                    var range = document.createRange();
                    range.selectNodeContents(el);
                    selection.removeAllRanges();
                    selection.addRange(range);
                    document.execCommand('Copy', 'false', null);
                    selection.removeAllRanges();
                });
                // Add
                $("#tree-menu li.add").off().on("click",function(e) {
                    e.stopPropagation();
                    $(this).hide().siblings("li.copy,li.edit,li.remove").hide();
                    $(this).siblings(".append,li.insertAfter,li.insertBefore").show();
                })
                // Added default data
                var obj={};
                obj[options.request.key]=_self.addKeyIndex;
                obj[options.request.name]="Name of the current action."+_self.nameIndex;
                
                var arr=["Append","InsertBefore","InsertAfter"];
                arr.forEach(function(val) {
                    var s=val[0].toLocaleLowerCase()+val.slice(1,val.length);
                    $("#tree-menu li."+s).off().on("click",function(e) {
                        var node=$(that).parent(".eleTree-node");
                        var key=node.attr("data-"+options.request.key);
                        key=isNaN(key) ? key : Number(key);
                        var isStop=false;
                        var s=val[0].toLocaleLowerCase()+val.slice(1,val.length);
                        layui.event.call(node, MOD_NAME, 'node'+val+'('+ _self.filter +')', {
                            node: node,
                            data: nodeData.currentData,
                            // Please set the data again
                            setData: function(o) {
                                _self[s](key,$.extend({},obj,o));
                                isStop=true;
                            },
                            // Adding stopped
                            stop: function() {
                                isStop=true;
                            }
                        });
                        if(isStop) return;
                        _self[s](key,obj)
                        _self.nameIndex++;
                        _self.addKeyIndex++;
                    })
                })
                
                // Edit
                $("#tree-menu li.edit").off().on("click",function(e) {
                    e.stopPropagation();
                    $("#tree-menu").hide().remove();
                    var node=$(that).parent(".eleTree-node");
                    var key=node.attr("data-"+options.request.key);
                    key=isNaN(key) ? key : Number(key);
                    var label=$(that).children(".eleTree-node-content-label").hide();
                    var text=label.text();
                    var inp="<input type='text' value='"+text+"' class='eleTree-node-content-input' />";
                    label.after(inp);
                    label.siblings(".eleTree-node-content-input").focus().select().off().on("blur",function() {
                        var val=$(this).val();
                        var isStop=false;
                        var inpThis=this;
                        layui.event.call(node, MOD_NAME, 'nodeEdit('+ _self.filter +')', {
                            node: node,
                            value: val,
                            data: nodeData.currentData,
                            // Adding stopped
                            stop: function() {
                                isStop=true;
                                $(inpThis).siblings(".eleTree-node-content-label").show();
                                $(inpThis).remove();
                            }
                        });
                        if(isStop) return;
                        // Edit Data
                        _self.reInitData(eleNode).currentData[options.request.name]=val;
                        // Editdom
                        $(this).siblings(".eleTree-node-content-label").text(val).show();
                        $(this).remove();
                    }).on("mousedown",function(e) {
                        // Preventinput拖拽
                        e.stopPropagation();
                    })
                })
                // 12
                $("#tree-menu li.remove").off().on("click",function(e) {
                    var node=$(that).parent(".eleTree-node");
                    var key=node.attr("data-"+options.request.key);
                    key=isNaN(key) ? key : Number(key);
                    var isStop=false;
                    layui.event.call(node, MOD_NAME, 'nodeRemove('+ _self.filter +')', {
                        node: node,
                        data: nodeData.currentData,
                        // Adding stopped
                        stop: function() {
                            isStop=true;
                        }
                    });
                    if(isStop) return;
                    _self.remove(key);
                })

                _self.prevClickEle=$(this);
            })
        },
        search: function(value) {
            var options=this.config;
            if(!options.searchNodeMethod || typeof options.searchNodeMethod !== "function"){
                return;
            }
            var data=options.data;
            // Data Retention
            var traverse=function(data) {
                data.forEach(function(val,index) {
                    // Add properties to all found nodes
                    val.visible=options.searchNodeMethod(value,val);
                    if(val[options.request.children] && val[options.request.children].length>0){
                        traverse(val[options.request.children]);
                    }
                    //If the current node's properties are hidden，Is the assistant's child node visible，If a question does not make any sense, or is not factually coherent, explain why instead of answering something not correct. If you don't know the answer to a question, please don't share false information.，Then the current node will be displayed
                    if(!val.visible){
                        let childSomeShow = false;
                        if(val[options.request.children] && val[options.request.children].length>0){
                            childSomeShow=val[options.request.children].some(function(v,i) {
                                return v.visible;
                            })
                        }
                        val.visible = childSomeShow;
                    }
                    // Through the properties of the node，Show hidden nodes，Adding the search provider failed
                    var el=options.elem.find("[data-"+options.request.key+"='"+val[options.request.key]+"']");
                    if(val.visible){
                        el.removeClass("eleTree-search-hide");
                        // Is the father node expanded，If the parent node is not expanded，Do not show me this dialog again
                        var parentEl=el.parent(".eleTree-node-group").parent(".eleTree-node");
                        var isParentOpen=parentEl.children(".eleTree-node-content").children(".eleTree-node-content-icon").children(".layui-icon.layui-icon-triangle-r").hasClass("icon-rotate")
                        if((parentEl.length>0 && isParentOpen) || parentEl.length===0){
                            el.show();
                        }
                    }else{
                        el.hide().addClass("eleTree-search-hide");
                    }
                    // Add Layer Attributes
                    if(val[options.request.children] && val[options.request.children].length>0){
                        val[options.request.children].forEach(function(v,i) {
                            delete v.visible;
                        })
                    }
                })
            }
            traverse(data);
            // Remove the outermost property
            var arr=[];
            data.forEach(function(val) {
                arr.push(val.visible);
                delete val.visible;
            })
            // If the first layer's all hidden，Text to show in the assistant when no default application is found to handle the type of file the user has selected.
            if(arr.every(function(v) {
                return v===false;
            })){
                laytpl(TPL_NoText()).render(options, function(string){
                    options.elem.append(string);
                }); 
            }else{
                options.elem.children(".eleTree-noText").remove();
            }
        }
    }
    
    exports(MOD_NAME,eleTree);
})