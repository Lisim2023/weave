use weave_example;
-- 用户表
drop table if exists sys_user;
create table sys_user (
  id                bigint  unsigned    not null auto_increment     comment '用户ID',
  username          varchar(30)         not null                    comment '用户名',
  nickname          varchar(30)         not null                    comment '用户昵称',
  password          varchar(100)        default ''                  comment '密码',
  gender            tinyint unsigned    not null                    comment '用户性别（0女 1男）',
  hobbies           varchar(50)         default ''                  comment '爱好',
  avatar            varchar(100)        default ''                  comment '头像地址',
  email             varchar(50)         default ''                  comment '用户邮箱',
  address           varchar(100)        default ''                  comment '用户地址',
  enabled           tinyint unsigned    default 0                   comment '用户状态（0正常 1停用）',
  create_by         bigint  unsigned    default null                comment '创建者',
  create_time       datetime                                        comment '创建时间',
  update_by         bigint  unsigned    default null                comment '更新者',
  update_time       datetime                                        comment '更新时间',
  del_flag          tinyint unsigned    default 0                   comment '删除标志（0正常 1已删除）',
  primary key (id)
) engine=innodb comment = '用户信息表';

-- 用户表数据
insert into sys_user values(1, 'admin', '管理员', '', 1, '1,12', '', '', '', 0, null, null, null, null, 0);
insert into sys_user values(2, 'xyrb', '轩辕如冰', '', 1, '3,14', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(3, 'ysdyw', '友善的远望', '', 1, '5,7,16', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(4, 'lqls', '蓝骑罗丝', '', 1, '8', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(5, 'zpdb', '棕牌豆包', '', 1, '6,10', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(6, 'xhkll', '夏侯卡罗琳', '', 1, '11,12', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(7, 'csdyl', '炒熟的又菱', '', 1, '3,8,15', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(8, 'jwc', '景伟宸', '', 1, '6,11', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(9, 'zkle', '卓克莱儿', '', 1, '5,13', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(10, 'smby', '水母宝莹', '', 1, '4,7,10', '', '', '', 0, 1, sysdate(), null, null, 0);

insert into sys_user values(11, 'fdt', '符断天', '', 1, '7,15', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(12, 'ppdj', '胖胖的荆', '', 1, '6,10,11', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(13, 'csjt', '橙石剑通', '', 1, '2,13', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(14, 'hfmtl', '皇甫曼陀罗', '', 1, '4,15', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(15, 'cxwn', '橙系温妮', '', 1, '1,7,12', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(16, 'hphn', '灰牌汉纳', '', 1, '3,5,12', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(17, 'bslbt', '伯赏罗伯塔', '', 1, '7,8,9,10', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(18, 'jy', '景芸', '', 1, '9,12', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(19, 'lwbd', '六尾贝蒂', '', 1, '3,14', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(20, 'lxbe', '罗希贝儿', '', 1, '15', '', '', '', 0, 1, sysdate(), null, null, 0);

insert into sys_user values(21, 'sqajs', '三千阿加莎', '', 1, '1,9', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(22, 'xkly', '席克洛怡', '', 1, '3,7,16', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(23, 'hxslm', '海星莎洛姆', '', 1, '1,7', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(24, 'cyb', '昌苑博', '', 1, '3,9,10', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(25, 'hxsm', '黑系树莓', '', 1, '11,12', '', '', '', 0, 1, sysdate(), null, null, 0);
insert into sys_user values(26, 'qzmld', '青钻米兰达', '', 1, '1,4,10,16', '', '', '', 0, 1, sysdate(), null, null, 0);




-- 角色表
drop table if exists sys_role;
create table sys_role (
  id                bigint  unsigned    not null auto_increment     comment '角色ID',
  name              varchar(30)         not null                    comment '角色名称',
  code              varchar(100)        not null                    comment '角色权限字符串',
  description       varchar(500)        default ''                  comment '角色描述',
  enabled           tinyint unsigned    default 0                   comment '角色状态（0正常 1停用）',
  create_by         bigint  unsigned    default null                comment '创建者',
  create_time       datetime                                        comment '创建时间',
  update_by         bigint  unsigned    default null                comment '更新者',
  update_time       datetime                                        comment '更新时间',
  del_flag          tinyint unsigned    default 0                   comment '删除标志（0正常 1已删除）',
  primary key (id)
) engine=innodb comment = '角色信息表';

-- 角色表数据
insert into sys_role values(1, '系统管理员',  'admin',       '', 0, 1, sysdate(), null, null, 0);
insert into sys_role values(2, '侦探冒险家',  'detective',   '', 0, 1, sysdate(), null, null, 0);
insert into sys_role values(3, '探索冒险家',  'exploration', '', 0, 1, sysdate(), null, null, 0);
insert into sys_role values(4, '竞技冒险家',  'competitive', '', 0, 1, sysdate(), null, null, 0);
insert into sys_role values(5, '休闲冒险家',  'casual',      '', 0, 1, sysdate(), null, null, 0);




-- 用户和角色关联表
drop table if exists sys_role_user;
create table sys_role_user (
   id                bigint  unsigned    not null auto_increment     comment '角色用户ID',
   role_id           bigint  unsigned    not null                    comment '角色ID',
   user_id           bigint  unsigned    not null                    comment '用户ID',
   primary key (id)
) engine=innodb comment = '用户和角色关联表';

-- 用户和角色关联表数据
insert into sys_role_user values (1, 1, 1);
insert into sys_role_user values (2, 1, 2);
insert into sys_role_user values (3, 1, 3);
insert into sys_role_user values (4, 1, 4);
insert into sys_role_user values (5, 1, 5);

insert into sys_role_user values (6, 2, 1);
insert into sys_role_user values (7, 2, 2);
insert into sys_role_user values (8, 2, 3);
insert into sys_role_user values (9, 2, 4);
insert into sys_role_user values (10, 2, 5);
insert into sys_role_user values (11, 2, 6);
insert into sys_role_user values (12, 2, 7);
insert into sys_role_user values (13, 2, 8);

insert into sys_role_user values (14, 3, 7);
insert into sys_role_user values (15, 3, 8);
insert into sys_role_user values (16, 3, 9);
insert into sys_role_user values (17, 3, 10);
insert into sys_role_user values (18, 3, 11);
insert into sys_role_user values (19, 3, 12);
insert into sys_role_user values (20, 3, 13);

insert into sys_role_user values (21, 4, 12);
insert into sys_role_user values (22, 4, 13);
insert into sys_role_user values (23, 4, 14);
insert into sys_role_user values (24, 4, 15);
insert into sys_role_user values (25, 4, 16);
insert into sys_role_user values (26, 4, 17);
insert into sys_role_user values (27, 4, 18);
insert into sys_role_user values (28, 4, 19);
insert into sys_role_user values (29, 4, 20);
insert into sys_role_user values (30, 4, 21);

insert into sys_role_user values (31, 5, 19);
insert into sys_role_user values (32, 5, 20);
insert into sys_role_user values (33, 5, 21);
insert into sys_role_user values (34, 5, 22);
insert into sys_role_user values (35, 5, 23);
insert into sys_role_user values (36, 5, 24);
insert into sys_role_user values (37, 5, 25);
insert into sys_role_user values (38, 5, 26);





-- 菜单表
drop table if exists sys_menu;
create table sys_menu (
  id                bigint  unsigned    not null auto_increment     comment '菜单ID',
  title             varchar(50)         not null                    comment '菜单标题',
  parent_id         bigint  unsigned    default null                comment '上级菜单ID',
  order_num         double(8,2)         default 0                   comment '显示顺序',
  path              varchar(200)        default ''                  comment '路由地址',
  component         varchar(255)        default ''                  comment '组件路径',
  type              tinyint unsigned    default null                comment '菜单类型（0目录 1菜单 2按钮）',
  perms             varchar(100)        default ''                  comment '权限标识',
  icon              varchar(100)        default ''                  comment '菜单图标',
  enabled           tinyint unsigned    default 0                   comment '菜单状态（0正常 1停用）',
  create_by         bigint  unsigned    default null                comment '创建者',
  create_time       datetime                                        comment '创建时间',
  update_by         bigint  unsigned    default null                comment '更新者',
  update_time       datetime                                        comment '更新时间',
  del_flag          tinyint unsigned    default 0                   comment '删除标志（0正常 1已删除）',
  primary key (id)
) engine=innodb comment = '菜单表';

-- 菜单表数据

insert into sys_menu values(1, '系统管理', null, 1, 'system',   '', 0, 'system',    '', 0, 1, sysdate(), null, null, 0);
insert into sys_menu values(2, '系统工具', null, 2, 'tool',     '', 0, 'tool',      '', 0, 1, sysdate(), null, null, 0);

insert into sys_menu values(101,  '用户管理', 1,   1, 'user',   'system/user/index',    1, 'system:user:list',      '', 0, 1, sysdate(), 1, sysdate(), 0);
insert into sys_menu values(102,  '角色管理', 1,   2, 'role',   'system/role/index',    1, 'system:role:list',      '', 0, 1, sysdate(), 5, sysdate(), 0);
insert into sys_menu values(103,  '菜单管理', 1,   3, 'menu',   'system/menu/index',    1, 'system:menu:list',      '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(104,  '部门管理', 1,   4, 'dept',   'system/dept/index',    1, 'system:dept:list',      '', 0, 1, sysdate(), 4, sysdate(), 0);
insert into sys_menu values(105,  '岗位管理', 1,   5, 'post',   'system/post/index',    1, 'system:post:list',      '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(106,  '字典管理', 1,   6, 'dict',   'system/dict/index',    1, 'system:dict:list',      '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(107,  '参数设置', 1,   7, 'config', 'system/config/index',  1, 'system:config:list',    '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(108,  '通知公告', 1,   8, 'notice', 'system/notice/index',  1, 'system:notice:list',    '', 0, 1, sysdate(), 5, sysdate(), 0);

insert into sys_menu values(201,  '定时任务', 2,   1, 'job',    'tool/job/index',   1, 'tool:job:list',     '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(202,  '数据监控', 2,   2, 'druid',  'tool/druid/index', 1, 'tool:druid:list',   '', 0, 1, sysdate(), 1, sysdate(), 0);
insert into sys_menu values(203,  '缓存监控', 2,   3, 'cache',  'tool/cache/index', 1, 'tool:cache:list',   '', 0, 1, sysdate(), 4, sysdate(), 0);

insert into sys_menu values(10101, '用户查询', 101, 1,  '', '', 2, 'system:user:query',     '', 0, 1, sysdate(), 1, sysdate(), 0);
insert into sys_menu values(10102, '用户新增', 101, 2,  '', '', 2, 'system:user:add',       '', 0, 1, sysdate(), 4, sysdate(), 0);
insert into sys_menu values(10103, '用户修改', 101, 3,  '', '', 2, 'system:user:edit',      '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10104, '用户删除', 101, 4,  '', '', 2, 'system:user:remove',    '', 0, 1, sysdate(), 4, sysdate(), 0);
insert into sys_menu values(10105, '用户导出', 101, 5,  '', '', 2, 'system:user:export',    '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(10106, '用户导入', 101, 6,  '', '', 2, 'system:user:import',    '', 0, 1, sysdate(), 1, sysdate(), 0);
insert into sys_menu values(10107, '重置密码', 101, 7,  '', '', 2, 'system:user:resetPwd',  '', 0, 1, sysdate(), 5, sysdate(), 0);

insert into sys_menu values(10201, '角色查询', 102, 1,  '', '', 2, 'system:role:query',     '', 0, 1, sysdate(), 1, sysdate(), 0);
insert into sys_menu values(10202, '角色新增', 102, 2,  '', '', 2, 'system:role:add',       '', 0, 1, sysdate(), 5, sysdate(), 0);
insert into sys_menu values(10203, '角色修改', 102, 3,  '', '', 2, 'system:role:edit',      '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10204, '角色删除', 102, 4,  '', '', 2, 'system:role:remove',    '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(10205, '角色导出', 102, 5,  '', '', 2, 'system:role:export',    '', 0, 1, sysdate(), 5, sysdate(), 0);

insert into sys_menu values(10301, '菜单查询', 103, 1,  '', '', 2, 'system:menu:query',     '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10302, '菜单新增', 103, 2,  '', '', 2, 'system:menu:add',       '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(10303, '菜单修改', 103, 3,  '', '', 2, 'system:menu:edit',      '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10304, '菜单删除', 103, 4,  '', '', 2, 'system:menu:remove',    '', 0, 1, sysdate(), 4, sysdate(), 0);

insert into sys_menu values(10401, '部门查询', 104, 1,  '', '', 2, 'system:dept:query',     '', 0, 1, sysdate(), 5, sysdate(), 0);
insert into sys_menu values(10402, '部门新增', 104, 2,  '', '', 2, 'system:dept:add',       '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10403, '部门修改', 104, 3,  '', '', 2, 'system:dept:edit',      '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(10404, '部门删除', 104, 4,  '', '', 2, 'system:dept:remove',    '', 0, 1, sysdate(), 1, sysdate(), 0);

insert into sys_menu values(10501, '岗位查询', 105, 1,  '', '', 2, 'system:post:query',     '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(10502, '岗位新增', 105, 2,  '', '', 2, 'system:post:add',       '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(10503, '岗位修改', 105, 3,  '', '', 2, 'system:post:edit',      '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10504, '岗位删除', 105, 4,  '', '', 2, 'system:post:remove',    '', 0, 1, sysdate(), 1, sysdate(), 0);
insert into sys_menu values(10505, '岗位导出', 105, 5,  '', '', 2, 'system:post:export',    '', 0, 1, sysdate(), 5, sysdate(), 0);

insert into sys_menu values(10601, '字典查询', 106, 1,  '', '', 2, 'system:dict:query',     '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10602, '字典新增', 106, 2,  '', '', 2, 'system:dict:add',       '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(10603, '字典修改', 106, 3,  '', '', 2, 'system:dict:edit',      '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10604, '字典删除', 106, 4,  '', '', 2, 'system:dict:remove',    '', 0, 1, sysdate(), 5, sysdate(), 0);
insert into sys_menu values(10605, '字典导出', 106, 5,  '', '', 2, 'system:dict:export',    '', 0, 1, sysdate(), 1, sysdate(), 0);

insert into sys_menu values(10701, '参数查询', 107, 1,  '', '', 2, 'system:config:query',   '', 0, 1, sysdate(), 4, sysdate(), 0);
insert into sys_menu values(10702, '参数新增', 107, 2,  '', '', 2, 'system:config:add',     '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10703, '参数修改', 107, 3,  '', '', 2, 'system:config:edit',    '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10704, '参数删除', 107, 4,  '', '', 2, 'system:config:remove',  '', 0, 1, sysdate(), 1, sysdate(), 0);
insert into sys_menu values(10705, '参数导出', 107, 5,  '', '', 2, 'system:config:export',  '', 0, 1, sysdate(), 2, sysdate(), 0);

insert into sys_menu values(10801, '公告查询', 108, 1,  '', '', 2, 'system:notice:query',   '', 0, 1, sysdate(), 4, sysdate(), 0);
insert into sys_menu values(10802, '公告新增', 108, 2,  '', '', 2, 'system:notice:add',     '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(10803, '公告修改', 108, 3,  '', '', 2, 'system:notice:edit',    '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(10804, '公告删除', 108, 4,  '', '', 2, 'system:notice:remove',  '', 0, 1, sysdate(), 5, sysdate(), 0);

insert into sys_menu values(20101, '任务查询', 201, 1,  '', '', 2, 'tool:job:query',        '', 0, 1, sysdate(), 5, sysdate(), 0);
insert into sys_menu values(20102, '任务新增', 201, 2,  '', '', 2, 'tool:job:add',          '', 0, 1, sysdate(), 4, sysdate(), 0);
insert into sys_menu values(20103, '任务修改', 201, 3,  '', '', 2, 'tool:job:edit',         '', 0, 1, sysdate(), 3, sysdate(), 0);
insert into sys_menu values(20104, '任务删除', 201, 4,  '', '', 2, 'tool:job:remove',       '', 0, 1, sysdate(), 4, sysdate(), 0);
insert into sys_menu values(20105, '状态修改', 201, 5,  '', '', 2, 'tool:job:changeStatus', '', 0, 1, sysdate(), 2, sysdate(), 0);
insert into sys_menu values(20106, '任务导出', 201, 6,  '', '', 2, 'tool:job:export',       '', 0, 1, sysdate(), 1, sysdate(), 0);





-- 字典表
drop table if exists sys_dict;
create table sys_dict
(
  id                bigint  unsigned    not null auto_increment     comment '字典主键',
  name              varchar(100)        default ''                  comment '字典名称',
  code              varchar(100)        default ''                  comment '字典编码',
  description       varchar(500)        default ''                  comment '描述',
  enabled           tinyint unsigned    default 0                   comment '字典状态（0正常 1停用）',
  create_by         bigint  unsigned    default null                comment '创建者',
  create_time       datetime                                        comment '创建时间',
  update_by         bigint  unsigned    default null                comment '更新者',
  update_time       datetime                                        comment '更新时间',
  del_flag          tinyint unsigned    default 0                   comment '删除标志（0正常 1已删除）',
  primary key (id),
  unique (code)
) engine=innodb comment = '字典表';

-- 字典表数据
insert into sys_dict values(1,  '用户性别', 'gender', '', 0, 1, sysdate(), null, null, 0);
insert into sys_dict values(2,  '是否', 'yn', '', 0, 1, sysdate(), null, null, 0);
insert into sys_dict values(3,  '删除状态', 'del_flag', '', 0, 1, sysdate(), null, null, 0);
insert into sys_dict values(4,  '菜单类型', 'menu_type', '', 0, 1, sysdate(), null, null, 0);
insert into sys_dict values(5,  '爱好', 'hobbies', '', 0, 1, sysdate(), null, null, 0);
insert into sys_dict values(6,  '受教育程度', 'edu_level', '', 0, 1, sysdate(), null, null, 0);
insert into sys_dict values(7,  '民族', 'nation', '', 0, 1, sysdate(), null, null, 0);
insert into sys_dict values(8,  '婚姻状况', 'marital_status', '', 0, 1, sysdate(), null, null, 0);




-- 字典数据表
drop table if exists sys_dict_item;
create table sys_dict_item
(
  id                bigint  unsigned    not null auto_increment     comment '字典项ID',
  dict_id           bigint  unsigned                                comment '所属字典ID',
  label             varchar(100)        not null                    comment '字典文本',
  value             int unsigned        not null                    comment '字典键值',
  order_num         double(8,2)         default 0                   comment '字典排序',
  description       varchar(500)        default ''                  comment '描述',
  enabled           tinyint unsigned    default 0                   comment '字典项状态（0正常 1停用）',
  create_by         bigint  unsigned    default null                comment '创建者',
  create_time       datetime                                        comment '创建时间',
  update_by         bigint  unsigned    default null                comment '更新者',
  update_time       datetime                                        comment '更新时间',
  primary key (id)
) engine=innodb comment = '字典数据表';

insert into sys_dict_item values(1,  1,  '女',       0,       1, '',           0, 1, sysdate(), 1, sysdate());
insert into sys_dict_item values(2,  1,  '男',       1,       2, '',           0, 1, sysdate(), 1, sysdate());

insert into sys_dict_item values(3,  2,  '否',       0,       1, '',          0, 1, sysdate(), 2, sysdate());
insert into sys_dict_item values(4,  2,  '是',       1,       2, '',          0, 1, sysdate(), 3, sysdate());

insert into sys_dict_item values(5,  3,  '正常',     0,       1, '',          0, 1, sysdate(), 3, sysdate());
insert into sys_dict_item values(6,  3,  '已删除',   1,       2, '',          0, 1, sysdate(), 6, sysdate());

insert into sys_dict_item values(7,  4,  '目录',     0,       1, '',          0, 1, sysdate(), 15, sysdate());
insert into sys_dict_item values(8,  4,  '菜单',     1,       2, '',          0, 1, sysdate(), 23, sysdate());
insert into sys_dict_item values(9,  4,  '按钮',     2,       3, '',          0, 1, sysdate(), 4, sysdate());

insert into sys_dict_item values(10, 5,  '篮球',     0,       1, '',          0, 1, sysdate(), 21, sysdate());
insert into sys_dict_item values(11, 5,  '足球',     1,       2, '',          0, 1, sysdate(), 22, sysdate());
insert into sys_dict_item values(12, 5,  '排球',     2,       3, '',          0, 1, sysdate(), 25, sysdate());
insert into sys_dict_item values(13, 5,  '桌球',     3,       4, '',          0, 1, sysdate(), 13, sysdate());
insert into sys_dict_item values(14, 5,  '跑步',     4,       5, '',          0, 1, sysdate(), 6, sysdate());
insert into sys_dict_item values(15, 5,  '滑板',     5,       6, '',          0, 1, sysdate(), 7, sysdate());
insert into sys_dict_item values(16, 5,  '跳绳',    6,       7, '',          0, 1, sysdate(), 16, sysdate());
insert into sys_dict_item values(17, 5,  '音乐',    7,       8, '',          0, 1, sysdate(), 22, sysdate());
insert into sys_dict_item values(18, 5,  '游戏',    8,       9, '',          0, 1, sysdate(), 17, sysdate());
insert into sys_dict_item values(19, 5,  '阅读',    9,       10, '',          0, 1, sysdate(), 23, sysdate());
insert into sys_dict_item values(20, 5,  '编程',    10,       11, '',          0, 1, sysdate(), 9, sysdate());

insert into sys_dict_item values(21, 5,  '影视',    11,       12, '',          0, 1, sysdate(), 25, sysdate());
insert into sys_dict_item values(22, 5,  '音乐',    12,       13, '',          0, 1, sysdate(), 7, sysdate());
insert into sys_dict_item values(23, 5,  '其他',    13,       14, '',          0, 1, sysdate(), 5, sysdate());
insert into sys_dict_item values(24, 5,  '骑马',    14,       15, '',          0, 1, sysdate(), 16, sysdate());
insert into sys_dict_item values(25, 5,  '网球',    15,       16, '',          0, 1, sysdate(), 23, sysdate());
insert into sys_dict_item values(26, 5,  '羽毛球',   16,       17, '',          0, 1, sysdate(), 22, sysdate());

insert into sys_dict_item values(27, 6,  '小学',   10,       1, '',          0, 1, sysdate(), 1, sysdate());
insert into sys_dict_item values(28, 6,  '初中',   20,       2, '',          0, 1, sysdate(), 10, sysdate());
insert into sys_dict_item values(29, 6,  '高中',   30,       3, '',          0, 1, sysdate(), 24, sysdate());
insert into sys_dict_item values(30, 6,  '中专',   35,       4, '',          0, 1, sysdate(), 14, sysdate());

insert into sys_dict_item values(31, 6,  '大专',   40,       5, '',          0, 1, sysdate(), 13, sysdate());
insert into sys_dict_item values(32, 6,  '本科',   50,       6, '',          0, 1, sysdate(), 7, sysdate());
insert into sys_dict_item values(33, 6,  '硕士研究生',   60,  7, '',          0, 1, sysdate(), 1, sysdate());
insert into sys_dict_item values(34, 6,  '硕士',   65,       8, '',          0, 1, sysdate(), 2, sysdate());
insert into sys_dict_item values(35, 6,  '博士研究生',   70,  9, '',          0, 1, sysdate(), 9, sysdate());
insert into sys_dict_item values(36, 6,  '博士',   75,       10, '',          0, 1, sysdate(), 13, sysdate());
insert into sys_dict_item values(37, 6,  '博士后',  90,       11, '',          0, 1, sysdate(), 18, sysdate());

insert into sys_dict_item values(38, 7,  '汉族',  1,       1, '',          0, 1, sysdate(), 24, sysdate());
insert into sys_dict_item values(39, 7,  '满族',  2,       2, '',          0, 1, sysdate(), 17, sysdate());
insert into sys_dict_item values(40, 7,  '蒙古族',  3,       3, '',          0, 1, sysdate(), 19, sysdate());

insert into sys_dict_item values(41, 7,  '回族',  4,       4, '',          0, 1, sysdate(), 2, sysdate());
insert into sys_dict_item values(42, 7,  '藏族',  5,       5, '',          0, 1, sysdate(), 3, sysdate());
insert into sys_dict_item values(43, 7,  '维吾尔族',  6,       6, '',          0, 1, sysdate(), 6, sysdate());
insert into sys_dict_item values(44, 7,  '苗族',  7,       7, '',          0, 1, sysdate(), 8, sysdate());
insert into sys_dict_item values(45, 7,  '彝族',  8,       8, '',          0, 1, sysdate(), 14, sysdate());
insert into sys_dict_item values(46, 7,  '壮族',  9,       9, '',          0, 1, sysdate(), 12, sysdate());
insert into sys_dict_item values(47, 7,  '布依族',  10,      10, '',          0, 1, sysdate(), 21, sysdate());
insert into sys_dict_item values(48, 7,  '侗族',  11,      11, '',          0, 1, sysdate(), 17, sysdate());
insert into sys_dict_item values(49, 7,  '瑶族',  12,      12, '',          0, 1, sysdate(), 1, sysdate());
insert into sys_dict_item values(50, 7,  '白族',  13,      13, '',          0, 1, sysdate(), 5, sysdate());

insert into sys_dict_item values(51, 7,  '土家族',  14,       14, '',          0, 1, sysdate(), 7, sysdate());
insert into sys_dict_item values(52, 7,  '哈尼族',  15,      15, '',          0, 1, sysdate(), 4, sysdate());
insert into sys_dict_item values(53, 7,  '哈萨克族',  16,      16, '',          0, 1, sysdate(), 14, sysdate());
insert into sys_dict_item values(54, 7,  '傣族',  17,      17, '',          0, 1, sysdate(), 19, sysdate());
insert into sys_dict_item values(55, 7,  '黎族',  18,      18, '',          0, 1, sysdate(), 18, sysdate());
insert into sys_dict_item values(56, 7,  '傈僳族',  19,      19, '',          0, 1, sysdate(), 24, sysdate());
insert into sys_dict_item values(57, 7,  '佤族',  20,      20, '',          0, 1, sysdate(), 15, sysdate());
insert into sys_dict_item values(58, 7,  '畲族',  21,      21, '',          0, 1, sysdate(), 22, sysdate());
insert into sys_dict_item values(59, 7,  '高山族',  22,      22, '',          0, 1, sysdate(), 3, sysdate());
insert into sys_dict_item values(60, 7,  '拉祜族',  23,      23, '',          0, 1, sysdate(), 6, sysdate());

insert into sys_dict_item values(61, 7,  '水族',  24,      24, '',          0, 1, sysdate(), 11, sysdate());
insert into sys_dict_item values(62, 7,  '东乡族',  25,      25, '',          0, 1, sysdate(), 15, sysdate());
insert into sys_dict_item values(63, 7,  '纳西族',  26,      26, '',          0, 1, sysdate(), 12, sysdate());
insert into sys_dict_item values(64, 7,  '景颇族',  27,      27, '',          0, 1, sysdate(), 21, sysdate());
insert into sys_dict_item values(65, 7,  '柯尔克孜族',  28,      28, '',          0, 1, sysdate(), 15, sysdate());
insert into sys_dict_item values(66, 7,  '土族',  29,      29, '',          0, 1, sysdate(), 7, sysdate());
insert into sys_dict_item values(67, 7,  '达斡尔族',  30,      30, '',          0, 1, sysdate(), 9, sysdate());
insert into sys_dict_item values(68, 7,  '仫佬族',  31,      31, '',          0, 1, sysdate(), 14, sysdate());
insert into sys_dict_item values(69, 7,  '羌族',  32,      32, '',          0, 1, sysdate(), 12, sysdate());
insert into sys_dict_item values(70, 7,  '布朗族',  33,      33, '',          0, 1, sysdate(), 6, sysdate());

insert into sys_dict_item values(71, 7,  '撒拉族',  34,      34, '',          0, 1, sysdate(), 8, sysdate());
insert into sys_dict_item values(72, 7,  '毛南族',  35,      35, '',          0, 1, sysdate(), 20, sysdate());
insert into sys_dict_item values(73, 7,  '仡佬族',  36,      36, '',          0, 1, sysdate(), 18, sysdate());
insert into sys_dict_item values(74, 7,  '锡伯族',  37,      37, '',          0, 1, sysdate(), 10, sysdate());
insert into sys_dict_item values(75, 7,  '阿昌族',  38,      38, '',          0, 1, sysdate(), 13, sysdate());
insert into sys_dict_item values(76, 7,  '普米族',  39,      39, '',          0, 1, sysdate(), 6, sysdate());
insert into sys_dict_item values(77, 7,  '朝鲜族',  40,      40, '',          0, 1, sysdate(), 3, sysdate());
insert into sys_dict_item values(78, 7,  '塔吉克族',  41,      41, '',          0, 1, sysdate(), 1, sysdate());
insert into sys_dict_item values(79, 7,  '怒族',  42,      42, '',          0, 1, sysdate(), 1, sysdate());
insert into sys_dict_item values(80, 7,  '乌孜别克族',  43,      43, '',          0, 1, sysdate(), 4, sysdate());

insert into sys_dict_item values(81, 7,  '俄罗斯族',  44,      44, '',          0, 1, sysdate(), 14, sysdate());
insert into sys_dict_item values(82, 7,  '鄂温克族',  45,      45, '',          0, 1, sysdate(), 15, sysdate());
insert into sys_dict_item values(83, 7,  '德昂族',  46,      46, '',          0, 1, sysdate(), 3, sysdate());
insert into sys_dict_item values(84, 7,  '保安族',  47,      47, '',          0, 1, sysdate(), 5, sysdate());
insert into sys_dict_item values(85, 7,  '裕固族',  48,      48, '',          0, 1, sysdate(), 8, sysdate());
insert into sys_dict_item values(86, 7,  '京族',  49,      49, '',          0, 1, sysdate(), 16, sysdate());
insert into sys_dict_item values(87, 7,  '塔塔尔族',  50,      50, '',          0, 1, sysdate(), 19, sysdate());
insert into sys_dict_item values(88, 7,  '独龙族',  51,      51, '',          0, 1, sysdate(), 12, sysdate());
insert into sys_dict_item values(89, 7,  '鄂伦春族',  52,      52, '',          0, 1, sysdate(), 22, sysdate());
insert into sys_dict_item values(90, 7,  '赫哲族',  53,      53, '',          0, 1, sysdate(), 25, sysdate());

insert into sys_dict_item values(91, 7,  '门巴族',  54,      54, '',          0, 1, sysdate(), 20, sysdate());
insert into sys_dict_item values(92, 7,  '珞巴族',  55,      55, '',          0, 1, sysdate(), 4, sysdate());
insert into sys_dict_item values(93, 7,  '基诺族',  56,      56, '',          0, 1, sysdate(), 8, sysdate());

insert into sys_dict_item values(94, 8,  '未婚',  1,      1, '',          0, 1, sysdate(), 10, sysdate());
insert into sys_dict_item values(95, 8,  '已婚',  2,      2, '',          0, 1, sysdate(), 11, sysdate());
insert into sys_dict_item values(96, 8,  '离异',  3,      3, '',          0, 1, sysdate(), 21, sysdate());
insert into sys_dict_item values(97, 8,  '丧偶',  4,      4, '',          0, 1, sysdate(), 2, sysdate());