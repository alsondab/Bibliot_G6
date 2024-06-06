-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : jeu. 06 juin 2024 à 06:11
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `librarymanagementsystem`
--

-- --------------------------------------------------------

--
-- Structure de la table `admins`
--

CREATE TABLE `admins` (
  `id` int(11) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `email` varchar(20) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `password` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `admins`
--

INSERT INTO `admins` (`id`, `name`, `email`, `phone`, `address`, `password`) VALUES
(2, 'Alson', 'a', '12', 'co', 1111);

-- --------------------------------------------------------

--
-- Structure de la table `bookdetails`
--

CREATE TABLE `bookdetails` (
  `BookId` int(11) NOT NULL,
  `BookName` varchar(255) DEFAULT NULL,
  `Authors` varchar(255) DEFAULT NULL,
  `Quantity` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `bookdetails`
--

INSERT INTO `bookdetails` (`BookId`, `BookName`, `Authors`, `Quantity`) VALUES
(1, 'PYTHON', 'ALSON', 5),
(2, 'Java', 'DAB', 4),
(4, 'html', 'LUCK', 6),
(5, 'fg', 'fg', 2);

-- --------------------------------------------------------

--
-- Structure de la table `defaulterlist`
--

CREATE TABLE `defaulterlist` (
  `IssueId` int(11) NOT NULL,
  `BookId` int(11) DEFAULT NULL,
  `BookName` varchar(255) DEFAULT NULL,
  `UserId` int(11) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `issuedate` date DEFAULT NULL,
  `duedate` date DEFAULT NULL,
  `status` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `issuebooksdetails`
--

CREATE TABLE `issuebooksdetails` (
  `IssueId` int(11) NOT NULL,
  `BookId` int(11) DEFAULT NULL,
  `BookName` varchar(255) DEFAULT NULL,
  `UserId` int(11) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `issueDate` date DEFAULT NULL,
  `dueDate` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `issuebooksdetails`
--

INSERT INTO `issuebooksdetails` (`IssueId`, `BookId`, `BookName`, `UserId`, `userName`, `issueDate`, `dueDate`, `status`) VALUES
(1, 1, 'PYTHON', 1, NULL, '2024-06-04', '2024-06-11', 'return'),
(2, 2, 'Java', 2, NULL, '2024-06-02', '2024-06-03', 'Pending'),
(3, 4, 'html', 4, NULL, '2024-06-04', '2024-06-09', 'Pending');

-- --------------------------------------------------------

--
-- Structure de la table `returnbooks`
--

CREATE TABLE `returnbooks` (
  `ReturnId` int(11) NOT NULL,
  `BookId` int(11) DEFAULT NULL,
  `UserId` int(11) DEFAULT NULL,
  `ReturnDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `phone`, `address`, `password`) VALUES
(1, 'ALI', 'ALI@gmail.com', '0710145864', 'cocody', '0987'),
(2, 'DAB', 'DAB@', '88', 'CO', '4321'),
(4, 'LANA', 'L@', '22', 'WS', '123');

-- --------------------------------------------------------

--
-- Doublure de structure pour la vue `viewissuebooks`
-- (Voir ci-dessous la vue réelle)
--
CREATE TABLE `viewissuebooks` (
`BookId` int(11)
,`BookName` varchar(255)
,`UserId` int(11)
,`userName` varchar(255)
,`issueDate` date
,`dueDate` date
,`status` varchar(50)
);

-- --------------------------------------------------------

--
-- Structure de la vue `viewissuebooks`
--
DROP TABLE IF EXISTS `viewissuebooks`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `viewissuebooks`  AS SELECT `ibd`.`BookId` AS `BookId`, `bd`.`BookName` AS `BookName`, `ibd`.`UserId` AS `UserId`, `u`.`name` AS `userName`, `ibd`.`issueDate` AS `issueDate`, `ibd`.`dueDate` AS `dueDate`, `ibd`.`status` AS `status` FROM ((`issuebooksdetails` `ibd` join `bookdetails` `bd` on(`ibd`.`BookId` = `bd`.`BookId`)) join `users` `u` on(`ibd`.`UserId` = `u`.`id`)) WHERE `ibd`.`status` = 'pending' ;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `bookdetails`
--
ALTER TABLE `bookdetails`
  ADD PRIMARY KEY (`BookId`);

--
-- Index pour la table `defaulterlist`
--
ALTER TABLE `defaulterlist`
  ADD PRIMARY KEY (`IssueId`);

--
-- Index pour la table `issuebooksdetails`
--
ALTER TABLE `issuebooksdetails`
  ADD PRIMARY KEY (`IssueId`),
  ADD KEY `BookId` (`BookId`),
  ADD KEY `UserId` (`UserId`);

--
-- Index pour la table `returnbooks`
--
ALTER TABLE `returnbooks`
  ADD PRIMARY KEY (`ReturnId`),
  ADD KEY `fk_book` (`BookId`),
  ADD KEY `fk_user` (`UserId`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `admins`
--
ALTER TABLE `admins`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `bookdetails`
--
ALTER TABLE `bookdetails`
  MODIFY `BookId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `defaulterlist`
--
ALTER TABLE `defaulterlist`
  MODIFY `IssueId` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `issuebooksdetails`
--
ALTER TABLE `issuebooksdetails`
  MODIFY `IssueId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `returnbooks`
--
ALTER TABLE `returnbooks`
  MODIFY `ReturnId` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `issuebooksdetails`
--
ALTER TABLE `issuebooksdetails`
  ADD CONSTRAINT `issuebooksdetails_ibfk_1` FOREIGN KEY (`BookId`) REFERENCES `bookdetails` (`BookId`),
  ADD CONSTRAINT `issuebooksdetails_ibfk_2` FOREIGN KEY (`UserId`) REFERENCES `users` (`id`);

--
-- Contraintes pour la table `returnbooks`
--
ALTER TABLE `returnbooks`
  ADD CONSTRAINT `fk_book` FOREIGN KEY (`BookId`) REFERENCES `bookdetails` (`BookId`),
  ADD CONSTRAINT `fk_user` FOREIGN KEY (`UserId`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
