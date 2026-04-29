CREATE TABLE IF NOT EXISTS client (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      document_id VARCHAR(45) NOT NULL,
    name VARCHAR(100) NOT NULL,
    direction VARCHAR(100)
    );

CREATE TABLE IF NOT EXISTS product_type (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            name VARCHAR(100) NOT NULL,
    description VARCHAR(250)
    );

CREATE TABLE IF NOT EXISTS warehouse (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         name VARCHAR(100) NOT NULL,
    country VARCHAR(45) NOT NULL,
    ubication VARCHAR(45) NOT NULL
    );

CREATE TABLE IF NOT EXISTS port (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    name VARCHAR(45) NOT NULL,
    country VARCHAR(45) NOT NULL,
    ubication VARCHAR(45) NOT NULL,
    international TINYINT(1) NOT NULL DEFAULT 0
    );

CREATE TABLE IF NOT EXISTS shipment (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        client_id INT NOT NULL,
                                        product_type_id INT NOT NULL,
                                        quantity INT NOT NULL,
                                        registry_date DATETIME NOT NULL,
                                        delivery_date DATETIME NOT NULL,
                                        price DECIMAL(10,2) NOT NULL,
    guide_number VARCHAR(10) NOT NULL UNIQUE,
    price_discount DECIMAL(10,2),
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (product_type_id) REFERENCES product_type(id)
    );

CREATE TABLE IF NOT EXISTS land_shipment (
                                             shipment_id INT PRIMARY KEY,
                                             warehouse_id INT NOT NULL,
                                             vehicle_plate VARCHAR(6) NOT NULL,
    FOREIGN KEY (shipment_id) REFERENCES shipment(id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
    );

CREATE TABLE IF NOT EXISTS sea_shipment (
                                            shipment_id INT PRIMARY KEY,
                                            port_id INT NOT NULL,
                                            fleet_number VARCHAR(8) NOT NULL,
    FOREIGN KEY (shipment_id) REFERENCES shipment(id),
    FOREIGN KEY (port_id) REFERENCES port(id)
    );

CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(45) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
    );