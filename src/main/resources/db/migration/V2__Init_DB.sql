-- Добавление поля imageBase64 в таблицу users
alter table users add column image_base64 longtext;

-- Добавление связи Many to One с users в таблице story
alter table story
add foreign key (author_id) references users(id);

-- Добавление поля status в таблицу story
alter table story add column  status enum('DRAFT','PUBLISHED');

-- Изменение типа столбца text в таблице story
alter table story
    modify column text longtext;
