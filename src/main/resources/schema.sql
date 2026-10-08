create database if not exists library_management
    default character set utf8mb4
    collate utf8mb4_unicode_ci;

use library_management;

create table if not exists categories (
    id bigint primary key auto_increment,
    name varchar(60) not null unique,
    description varchar(200) null,
    created_at datetime not null default current_timestamp
);

create table if not exists books (
    id bigint primary key auto_increment,
    title varchar(100) not null,
    author varchar(80) not null,
    isbn varchar(32) null,
    publisher varchar(100) null,
    publish_year int null,
    total_copies int not null default 0,
    available_copies int not null default 0,
    category_id bigint null,
    shelf_location varchar(80) null,
    summary varchar(500) null,
    cover_url varchar(255) null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    constraint uk_books_isbn unique (isbn),
    constraint ck_books_total_copies check (total_copies >= 0),
    constraint ck_books_available_copies check (available_copies >= 0),
    constraint ck_books_available_lte_total check (available_copies <= total_copies)
);

set @schema_name = database();
set @sql = if((select count(*) from information_schema.columns where table_schema=@schema_name and table_name='books' and column_name='category_id') = 0, 'alter table books add column category_id bigint null', 'select 1');
prepare stmt from @sql; execute stmt; deallocate prepare stmt;
set @sql = if((select count(*) from information_schema.columns where table_schema=@schema_name and table_name='books' and column_name='shelf_location') = 0, 'alter table books add column shelf_location varchar(80) null', 'select 1');
prepare stmt from @sql; execute stmt; deallocate prepare stmt;
set @sql = if((select count(*) from information_schema.columns where table_schema=@schema_name and table_name='books' and column_name='summary') = 0, 'alter table books add column summary varchar(500) null', 'select 1');
prepare stmt from @sql; execute stmt; deallocate prepare stmt;
set @sql = if((select count(*) from information_schema.columns where table_schema=@schema_name and table_name='books' and column_name='cover_url') = 0, 'alter table books add column cover_url varchar(255) null', 'select 1');
prepare stmt from @sql; execute stmt; deallocate prepare stmt;

create table if not exists users (
    id bigint primary key auto_increment,
    username varchar(50) not null unique,
    password_hash varchar(100) not null,
    display_name varchar(80) not null,
    enabled boolean not null default true,
    created_at datetime not null default current_timestamp
);

create table if not exists roles (
    id bigint primary key auto_increment,
    code varchar(50) not null unique,
    name varchar(80) not null
);

create table if not exists permissions (
    id bigint primary key auto_increment,
    code varchar(80) not null unique,
    name varchar(100) not null
);

create table if not exists user_roles (
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint fk_user_roles_user foreign key (user_id) references users(id),
    constraint fk_user_roles_role foreign key (role_id) references roles(id)
);

create table if not exists role_permissions (
    role_id bigint not null,
    permission_id bigint not null,
    primary key (role_id, permission_id),
    constraint fk_role_permissions_role foreign key (role_id) references roles(id),
    constraint fk_role_permissions_permission foreign key (permission_id) references permissions(id)
);

create table if not exists borrow_records (
    id bigint primary key auto_increment,
    book_id bigint not null,
    user_id bigint not null,
    borrow_date date not null,
    due_date date not null,
    return_date date null,
    remark varchar(200) null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp on update current_timestamp,
    constraint fk_borrow_records_book foreign key (book_id) references books(id),
    constraint fk_borrow_records_user foreign key (user_id) references users(id)
);

insert into categories (name, description)
values
    ('计算机', '编程、系统、人工智能与软件工程'),
    ('教材', '课程教材和考试参考书'),
    ('文学', '小说、散文与人文阅读'),
    ('管理', '项目管理、组织管理与职业发展')
on duplicate key update description = values(description);

insert into books (title, author, isbn, publisher, publish_year, total_copies, available_copies, category_id, shelf_location, summary)
values
    ('Java Core Technology', 'Cay S. Horstmann', '9787111636663', 'China Machine Press', 2019, 5, 5, (select id from categories where name='计算机'), 'A-01-01', 'Java 基础与核心类库参考书。'),
    ('Computer Systems: A Programmer''s Perspective', 'Randal E. Bryant', '9787111544937', 'China Machine Press', 2016, 3, 2, (select id from categories where name='计算机'), 'A-01-02', '计算机系统、程序执行和底层原理经典教材。')
on duplicate key update title = values(title), category_id = values(category_id), shelf_location = values(shelf_location), summary = values(summary);

insert into roles (code, name)
values
    ('ADMIN', 'System Administrator'),
    ('LIBRARIAN', 'Librarian'),
    ('READER', 'Reader')
on duplicate key update name = values(name);

insert into permissions (code, name)
values
    ('book:read', 'Read books'),
    ('book:write', 'Create and update books'),
    ('book:delete', 'Delete books'),
    ('category:manage', 'Manage categories'),
    ('borrow:manage', 'Manage borrow records'),
    ('user:manage', 'Manage users')
on duplicate key update name = values(name);

insert into users (username, password_hash, display_name, enabled)
values
    ('admin', '{bcrypt}$2a$10$XZ8UE162SS6qqwtSdL/8MeL8AdIEOyErYIJ5l4aoOM261lF/RWS0W', 'Admin', true),
    ('reader', '{bcrypt}$2a$10$gy2wcHiipWiITaX7LtOJteO9POhdwd1nsRKIwu8FxAIOlUmIkqmFW', 'Reader', true)
on duplicate key update password_hash = values(password_hash), display_name = values(display_name), enabled = values(enabled);

insert ignore into user_roles (user_id, role_id)
select u.id, r.id from users u, roles r where u.username = 'admin' and r.code = 'ADMIN';
insert ignore into user_roles (user_id, role_id)
select u.id, r.id from users u, roles r where u.username = 'reader' and r.code = 'READER';

insert ignore into role_permissions (role_id, permission_id)
select r.id, p.id from roles r, permissions p
where r.code = 'ADMIN' and p.code in ('book:read', 'book:write', 'book:delete', 'category:manage', 'borrow:manage', 'user:manage');
insert ignore into role_permissions (role_id, permission_id)
select r.id, p.id from roles r, permissions p
where r.code = 'LIBRARIAN' and p.code in ('book:read', 'book:write', 'category:manage', 'borrow:manage');
insert ignore into role_permissions (role_id, permission_id)
select r.id, p.id from roles r, permissions p
where r.code = 'READER' and p.code = 'book:read';

