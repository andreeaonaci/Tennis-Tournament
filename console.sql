DROP DATABASE IF EXISTS tennis_tournament;
CREATE DATABASE tennis_tournament;

USE tennis_tournament;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('TENNIS_PLAYER', 'REFEREE', 'ADMINISTRATOR') NOT NULL
);

CREATE TABLE tournaments (
    tour_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    location VARCHAR(100) NOT NULL
);

CREATE TABLE user_enrollment (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    tournament_id INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(tour_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE matches (
    match_id INT AUTO_INCREMENT PRIMARY KEY,
    tournament_id INT NOT NULL,
    player1_id INT NOT NULL,
    player2_id INT NOT NULL,
    referee_id INT NOT NULL,
    match_date DATETIME NOT NULL,
    score VARCHAR(50) DEFAULT NULL,
    court VARCHAR(50) DEFAULT NULL,
    FOREIGN KEY (tournament_id) REFERENCES tournaments(tour_id) ON DELETE CASCADE,
    FOREIGN KEY (player1_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (player2_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (referee_id) REFERENCES users(user_id) ON DELETE CASCADE
);

INSERT INTO users (username, email, password, role) VALUES
    ('player1', 'player1@yahoo.com', '1234', 'TENNIS_PLAYER'),
    ('player2', 'player2@yahoo.com', '1234', 'TENNIS_PLAYER'),
    ('player3', 'player2@yahoo.com', '1234', 'TENNIS_PLAYER'),
    ('player4', 'player2@yahoo.com', '1234', 'TENNIS_PLAYER'),
    ('referee1', 'referee1@yahoo.com', '1234', 'REFEREE'),
    ('referee2', 'referee2@yahoo.com', '1234', 'REFEREE'),
    ('admin1', 'admin1@yahoo.com', '1234', 'ADMINISTRATOR');

INSERT INTO tournaments (name, start_date, end_date, location) VALUES
    ('Tournament 1', '2024-04-01', '2024-04-15', 'New York'),
    ('Tournament 2', '2024-05-01', '2024-05-15', 'Paris');

INSERT INTO matches (tournament_id, player1_id, player2_id, referee_id, match_date, score, court) VALUES
    (1, 1, 2, 5, '2024-04-05 09:00:00', '6-4, 7-6', 'Court1'),
    (1, 2, 3, 5, '2024-04-06 10:00:00', '6-2, 6-3', 'Court2'),
    (1, 3, 4, 6, '2024-04-06 16:00:00', '6-2, 6-3', 'Court2'),
    (2, 1, 4, 6, '2024-04-06 22:00:00', '6-2, 6-3', 'Court3');
