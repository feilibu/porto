
CREATE USER 'porto'@'localhost' IDENTIFIED BY 'porto';

GRANT ALL PRIVILEGES ON *.* TO 'porto'@'localhost' IDENTIFIED BY 'porto' WITH GRANT OPTION MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0 ;

GRANT ALL PRIVILEGES ON `porto_%`.* TO 'porto'@'localhost';

use porto;

CREATE TABLE `quotes` (
`symbol` VARCHAR( 10 ) NOT NULL ,
`name` VARCHAR( 50 ) NOT NULL ,
`day` DATE NULL ,
`high` DOUBLE NULL ,
`low` DOUBLE NULL ,
`open` DOUBLE NULL ,
`close` DOUBLE NULL ,
`volume` INT NULL
) ENGINE = MYISAM ;

/* todo : add tickers definition */

create table selection_name (
id integer auto_increment unique not null,
name varchar( 100 ) not null );

create table selection (
idf_selection_name integer ,
ticker varchar( 10 ) );

create table stocks(symbol varchar(10), name varchar(30), isin(varchar(30));

load data infile '../stocks.csv' into table stocks(symbol,name,isin);


insert into selection_name(name) values('current');

insert into selection(idf_selection_name,ticker) select 1, symbol from stocks;


