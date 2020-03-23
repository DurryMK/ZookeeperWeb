/**
 * show.html
 * 
 */

var vm = new Vue({
		el:"#App",
		data:{
			path:"/",
			theme:"blue",
			tree:null,
			content:null,
			show:0,
			key:null,
			nodeName:"/",
			button:"btn-primary"
		},
		methods:{
			//创建目录
			showTree(){
				var theme=this.theme;
				if(theme=='blue'){
					var onhoverColor = "#00BCD4";
					var backColor = "#206676";
					var searchResultColor ="#FF9900";
				}
				if(theme=='orange'){
					var onhoverColor = "#00BCD4";
					var backColor = "#FF9F4C";
					var searchResultColor ="#FFFF00";
				}
				if(theme=='white'){
					var onhoverColor = "#00BCD4";
					var backColor = "#E1EAE7";
					var searchResultColor ="#A6524D";
				}
				$('#tree').treeview({
					data : this.tree,
					levels:0,
					showBorder:true,
					onhoverColor:onhoverColor,
				    highlightSelected:true,
					collapseIcon:"glyphicon glyphicon-folder-open",
					expandIcon:"glyphicon glyphicon-folder-close",
					emptyIcon:"glyphicon glyphicon-list-alt",
					backColor:backColor,
					searchResultColor:searchResultColor
				});
				this.createClick();
			},
			//创造Tree的点击事件监听
			createClick(){
				$('#tree').on('nodeSelected', function(event, data) {
					vm.nodeName=data.text;
					vm.getPath(data,data.text);
					});
			},
			//搜索节点
			searchNode(){
				if(this.key==null||this.key.length==0){
					return;
				}
				//获取树
				var tree = $('#tree');
				//记录搜索关键字
				var key = this.key;
				//折叠所有节点
				tree.treeview('collapseAll', { silent: true });
				//清除搜索标记
				tree.treeview('clearSearch');
				//如果key为根节点
				if(this.key=="/"){
					this.nodeName = "/";
					this.path="/";
					this.getNodeInfo();
					return;
				}
				//获得搜索结果
				var nodes = tree.treeview('search', [ key]);
				//没有该节点时
				if(nodes.length==0){
					this.nodeName = "/";
					this.path="/";
					this.getNodeInfo();
					alert("找不到节点....");
					return;
				}
				//修改节点信息的标题
				this.nodeName = nodes[0].text;
				//定位到搜索结果的第一个
				$('#tree').treeview('selectNode', [ nodes[0].nodeId, { silent: true } ]);
				//显示第一个节点的路径和信息
				this.getPath(nodes[0],nodes[0].text);
			},
			//将path修改为当前节点的路径并显示path的节点信息
			getPath(data,path){
				path="/"+path;
				//获得当前节点的父节点
				var parent = $('#tree').treeview('getParent', data);
				//如果父节点不是根节点则递归
				//父节点id为undefinde
				if(typeof(parent.nodeId)!='undefined'){
					path = parent.text+path;
					this.getPath(parent,path);
				}else{
					this.path=path;
				}
				//显示path的节点信息
				this.getNodeInfo();
			},
			//获得path的节点信息
			getNodeInfo(){
				this.$http.post('../zk.do', {
					op : "getNodeInfo",
					path:this.path
				}, {
					emulateJSON : true
				}).then(function(res) {
					if(typeof(res.data.ctime)=='undefined'){
						this.show=2;
					}else{
						this.show=1;
						this.content=res.data;
					}
				});
			},
			//切换主题
			changeTheme(theme,buttoncolor){
				if(theme==this.theme){
					return;
				}
				this.theme=theme;
				this.button=buttoncolor;
				//修改主题
				this.setTheme(theme);
				//生成新主题的css文件路径
				var url = "../css/theme"+theme+".css";
				
				//删除当前的css文件
				var node = document.getElementsByTagName("link")[1];
				var head = document.getElementsByTagName("head")[0];
				head.removeChild(node);
				//加载新主题的css文件
				this.loadStyles(url);
				//删除原有的树
				$('#tree').treeview('remove');
				//新建树
				this.showTree();
			},
			//设置主题
			setTheme(theme){
				this.$http.post('../zk.do', {
					op : "setTheme",
					theme:theme
				}, {
					emulateJSON : true
				}).then(function(res) {
					return;
				});
			},
			//加载主题
			loadStyles(url) {
			    var link = document.createElement("link");
			    link.rel = "stylesheet";
			    link.type = "text/css";
			    link.href = url;
			    var head = document.getElementsByTagName("head")[0];
			    head.appendChild(link);
			}
		},
		mounted() {
			//得到页面主题
			this.$http.post('../zk.do', {
				op : "getTheme"
			}, {
				emulateJSON : true
			}).then(function(res) {
				var theme = res.data.theme;
				if(this.theme==theme){
					return ;
				}
				var buttoncolor = "";
				if(theme=="blue"){
					buttoncolor="btn-primary";
				}
				if(theme=="orange"){
					buttoncolor="btn-danger";
				}
				if(theme=="white"){
					buttoncolor="btn-default";
				}
				this.changeTheme(theme,buttoncolor);
			});
			//初始化界面
			this.$http.post('../zk.do', {
				op : "getTree",
				path:this.path
			}, {
				emulateJSON : true
			}).then(function(res) {
				this.show=0;
				this.getNodeInfo();
				this.tree=res.data;
				this.showTree();
			});
		}
		
	})