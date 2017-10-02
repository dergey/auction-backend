CREATE DATABASE auction CHARACTER SET utf8 COLLATE utf8_general_ci;
USE auction;

-- Сущность "Пользователи"
CREATE TABLE user (
  id        INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username  VARCHAR(255) NOT NULL,
  password  VARCHAR(255) NOT NULL,
  firstname VARCHAR(50),
  lastname  VARCHAR(50),
  email     VARCHAR(50),
  rating    SMALLINT(5),
  history   VARCHAR(255),
  notification_token VARCHAR(255)
)
  ENGINE = InnoDB;


-- Table: roles
CREATE TABLE roles (
  id   INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL
)
  ENGINE = InnoDB;

-- Table for mapping user and roles: user_roles
CREATE TABLE user_roles (
  user_id INT NOT NULL,
  role_id INT NOT NULL,

  FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,

  UNIQUE (user_id, role_id)
)
  ENGINE = InnoDB;

-- Сущность "Категории"
CREATE TABLE categories (
  id    INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(100) NOT NULL,
  image VARCHAR(255)
)
  ENGINE = InnoDB;

-- Сущность "Лоты"
CREATE TABLE lots (
  id                INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title             VARCHAR(100)  NOT NULL,
  description       VARCHAR(255),
  image             VARCHAR(255),
  expiration_date   TIMESTAMP     NOT NULL,
  starting_price    DOUBLE        NOT NULL,
  auction_step      DOUBLE        NOT NULL,
  `status`          INT           NOT NULL,

  owner_id      INT NOT NULL,
  category_id   INT NOT NULL,

  FOREIGN KEY (owner_id) REFERENCES user (id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
)
  ENGINE = InnoDB;

-- Сущность "Ставки"
CREATE TABLE bids (
  id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id     INT NOT NULL,
  lot_id      INT NOT NULL,
  time        TIMESTAMP,
  size        DOUBLE,

  FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
  FOREIGN KEY (lot_id) REFERENCES lots (id) ON DELETE CASCADE
)
  ENGINE = InnoDB;

-- Добавление данных
INSERT INTO roles VALUES (1, 'ROLE_USER');
INSERT INTO roles VALUES (2, 'ROLE_ADMIN');

INSERT INTO user VALUES (1, 'dergey', '$2a$11$GXEpKRVDZwA6t8nopOZme./brH1F2o1POOd7DL5JYXos07OSniGzK',
                          'Сергей', 'Журавлёв', 'dergey@gmail.com', 5, 'About me' , NULL);

INSERT INTO user_roles VALUES (1, 1);

INSERT INTO categories VALUE (NULL, 'Автомобили', 'assets:/category_auto.jpg');
INSERT INTO categories VALUE (NULL, 'Антиквариат', 'assets:/category_anti.jpg');
INSERT INTO categories VALUE (NULL, 'Детские товары','assets:/category_baby.jpg');
INSERT INTO categories VALUE (NULL, 'Запчасти для автомобилей','assets:/category_detail.jpg');
INSERT INTO categories VALUE (NULL, 'Искусство','assets:/category_pict.jpg');
INSERT INTO categories VALUE (NULL, 'Книги','assets:/category_book.jpg');
INSERT INTO categories VALUE (NULL, 'Компьютеры','assets:/category_comp.jpg');
INSERT INTO categories VALUE (NULL, 'Красота и здоровье','assets:/category_heals.jpg');
INSERT INTO categories VALUE (NULL, 'Мобильные телефоны','assets:/category_mob.jpg');
INSERT INTO categories VALUE (NULL, 'Монеты', 'assets:/category_coins.jpg');
INSERT INTO categories VALUE (NULL, 'Недвижимость', 'assets:/category_nedv.jpg');
INSERT INTO categories VALUE (NULL, 'Обувь', 'assets:/category_obyv.jpg');
INSERT INTO categories VALUE (NULL, 'Одежда', 'assets:/category_suit.jpg');
INSERT INTO categories VALUE (NULL, 'Подарки, сувениры', 'assets:/category_prezent.jpg');
INSERT INTO categories VALUE (NULL, 'Софт','assets:/category_soft.jpg');
INSERT INTO categories VALUE (NULL, 'Спорттовары','assets:/category_sport.jpg');
INSERT INTO categories VALUE (NULL, 'Техника','assets:/category_tech.jpg');
INSERT INTO categories VALUE (NULL, 'Товары для дома','assets:/category_home_tovar.jpg');
INSERT INTO categories VALUE (NULL, 'Разное', 'assets:/category_other.png');
