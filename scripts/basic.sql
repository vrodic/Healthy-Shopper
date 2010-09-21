CREATE TABLE Products (
  id  varchar(100) PRIMARY KEY,
  countrycodeorigin varchar(3),
  countrycodebarcode varchar(3),
  name	varchar(200),
  verified int,
  updated date
);

CREATE TABLE Enumbers ( 
    id varchar(5) PRIMARY KEY,
    name varchar (255),
    safety int,
    alergy int,
    notes int,
    vegetarian int,
    children int,
    details text
    
);

CREATE TABLE ProductsEnumbers (
    barcode varchar (100) References Products,
    enumber varchar(4) References Enumbers
);

CREATE TABLE Users (
    id SERIAL,
    
);
