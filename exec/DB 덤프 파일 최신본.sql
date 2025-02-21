-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: i12d106.p.ssafy.io    Database: munbangu
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `FCM`
--

DROP TABLE IF EXISTS `FCM`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `FCM` (
  `token_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `token` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `idx_user_id` (`user_id`),
  CONSTRAINT `FCM_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=427 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `FCM`
--

LOCK TABLES `FCM` WRITE;
/*!40000 ALTER TABLE `FCM` DISABLE KEYS */;
INSERT INTO `FCM` VALUES (1,1,'eEwucDKnTIeKDqkMI3TQux:APA91bHs3PDwwRA5l7ZkzGaJRQmJxu7uDrlw66UIS_ydv9lcFE5NRpmclq2Yt3RPnYHeOhLUdeV1z5gHyb8lMjMo71SCsRsDbV_dSC5BuHxrg9yQ2zQUL0c','2025-02-17 00:30:50','2025-02-20 04:15:34'),(2,3,'cI-CMRKpRZWzN6BFGEMYc3:APA91bHi9n9zkyrCclsrxwFRsgWC6q7LsfVfn74wpzMJksYbLzYvf3kWI5KHMkv8rElvqFYa7wXgYIaQDjyk_kt5qwkEplXv4ae40j9JThT-qE5h0g8vjQE','2025-02-17 00:42:01','2025-02-20 03:36:20'),(14,6,'eYwZi5MhTNW3pi7jrGZkob:APA91bE84iWPA9nQD3V10rA8FqcQuQCPs6D1XiK9v1SD2vOgu4ptZHztgShrE8uOvLdrBoWmBvRGrIr9f5z3BulYE34a7mNqRzGvYrKAEiZE5_Z3qWepTfU','2025-02-17 01:33:52','2025-02-17 01:33:52'),(23,7,'eRlj0JkbSTOVGHRfUoc2Gp:APA91bHnslrua118kQ9Cqypaa0olCnzlL0sJLd7yev399VuM0xFSMpuwJyzDQhm5KoS3amTdgKZ0aF4S4UB7SnlcAS3YyPYFxc3wpHpP9EGtHJTevfN65zo','2025-02-17 04:21:41','2025-02-20 10:05:43'),(76,8,'f8Qc1mgwQZ2mIFR_8Bvz6z:APA91bEUUOpSvQ4L8FGEbqV_CewC0lWUCiHilqlSRQziqpopKvknXde2Aq-nOyvJTTmvvCMtZv96ENFeeWR_o-0gpLtIA4oAiJigUbAg_thbc8iy8x37i2U','2025-02-17 08:36:01','2025-02-19 03:38:41'),(123,12,'fuHGQfdaQxO3J1g2iEm1Bz:APA91bGibnxbjhmMrJnI9LVeDsuAVdCeCB12264SMk-uC73P2KkD8XARvx7HAd22hmuqUxhHfJUQpuNxUE4mU9ozrdk6HIeb2BGsap8fvW0cAdRfiPhVZjI','2025-02-18 03:13:49','2025-02-19 06:00:20'),(239,14,'d5J2iCihSNu2xhuOJhkb6E:APA91bHIDyGh8c2ewut1wkU5nBokUF1RcLYoYQ0mDlRzvMA_Dt4u_X233cQusvHlx1NZPeaLLMiEEJqSYMmRWZ6cR2FzAld58QGykx5DIQPDoGvUW-1UBCc','2025-02-19 03:39:14','2025-02-20 03:35:10'),(254,15,'eRlj0JkbSTOVGHRfUoc2Gp:APA91bHnslrua118kQ9Cqypaa0olCnzlL0sJLd7yev399VuM0xFSMpuwJyzDQhm5KoS3amTdgKZ0aF4S4UB7SnlcAS3YyPYFxc3wpHpP9EGtHJTevfN65zo','2025-02-19 04:17:17','2025-02-20 10:28:26');
/*!40000 ALTER TABLE `FCM` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alarms`
--

DROP TABLE IF EXISTS `alarms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarms` (
  `alarm_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` varchar(1000) NOT NULL,
  `sent_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`alarm_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `alarms_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarms`
--

LOCK TABLES `alarms` WRITE;
/*!40000 ALTER TABLE `alarms` DISABLE KEYS */;
/*!40000 ALTER TABLE `alarms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cards`
--

DROP TABLE IF EXISTS `cards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cards` (
  `card_id` bigint NOT NULL AUTO_INCREMENT,
  `code_id` varchar(10) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `image_url` varchar(255) NOT NULL,
  PRIMARY KEY (`card_id`),
  KEY `code_id` (`code_id`),
  CONSTRAINT `cards_ibfk_1` FOREIGN KEY (`code_id`) REFERENCES `common_codes` (`code_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cards`
--

LOCK TABLES `cards` WRITE;
/*!40000 ALTER TABLE `cards` DISABLE KEYS */;
INSERT INTO `cards` VALUES (1,'M001','일월오봉도','heritage/cards/4a6d9edd-8473-4dfc-9473-fab63681eb06_image 53.png'),(2,'M001','백자','heritage/cards/29d6606d-5a55-40f8-8904-4c5e92626045_image 54.png'),(3,'M001','어보','heritage/cards/06437240-a71f-473f-8d18-e3de474a6782_image 55.png'),(4,'M001','향로','heritage/cards/c7951418-bd31-4cc8-aa3a-9e7f517e3cd8_image 63.png'),(5,'M001','촛대','heritage/cards/d3df6b2c-ec36-4e64-ac35-f6db822b7ed3_image 57.png'),(6,'M001','붓걸이','heritage/cards/122cbd94-bc7b-4ae8-b165-9a8b75caf8f0_image 58.png'),(7,'M001','연적','heritage/cards/b5ba4abb-76bd-4325-a087-6eedef63f6a8_image 59.png'),(8,'M001','드므','heritage/cards/0cc20b5d-5446-48c9-a17c-29b64d8babe4_image 60.png'),(9,'M001','화로','heritage/cards/50ce9777-d6bc-4f08-afd9-1b3d592a098e_image 61.png'),(10,'M001','해태상','heritage/cards/7a88df15-bce5-4c92-a784-df6b5592b16e_image 62.png'),(11,'M002','사도세자의 눈물 젖은 뒤주','story/cards/450ea06b-7c6f-499a-b57e-c976c6581c6e_image 6.png'),(12,'M002','광해군의 부서진 어좌','story/cards/b7dbd8e4-3d18-4b97-8b09-c7440f24f78c_image 7.png'),(13,'M002','정몽주의 운명이 걸린 선죽교','story/cards/a8367c81-99af-4c5d-85d6-a3fbfce95634_image 8.png'),(14,'M002','관창의 주인 잃은 말','story/cards/aff2a864-9dfb-4c23-b7f8-7faed30de9de_image 9.png'),(15,'M002','안중근의 녹슨 권총','story/cards/f2fe10f5-4d72-4b1a-8ff5-e59a59cf3bdb_image 10.png'),(16,'M002','이방원의 차갑게 식어버린 철퇴 (사용 후)','story/cards/243b4e23-4a88-49dd-ae9b-c2d4c3580db4_image 43.png'),(17,'M002','세조의 자존심을 흔든 통소','story/cards/13105acb-a7d2-4b0b-a5ac-d3c111b249f1_image 44.png'),(18,'M002','정조의 관심받은 송충이','story/cards/3bc6c37e-80e3-42ce-baa1-70f8e1bead2e_image 50.png'),(19,'M002','흥선대원군의 완고한 척화비','story/cards/13a7cff4-76c8-435f-8910-355e59e2fc06_image 52.png'),(20,'M002','경회루의 굳건한 돌기둥','story/cards/200759fb-f2a2-42c7-aa21-55b53bd0a41e_image 63.png');
/*!40000 ALTER TABLE `cards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `common_codes`
--

DROP TABLE IF EXISTS `common_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `common_codes` (
  `code_id` varchar(10) NOT NULL,
  `category` varchar(50) NOT NULL,
  PRIMARY KEY (`code_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `common_codes`
--

LOCK TABLES `common_codes` WRITE;
/*!40000 ALTER TABLE `common_codes` DISABLE KEYS */;
INSERT INTO `common_codes` VALUES ('J001','팀장'),('J002','팀원'),('M001','문화재'),('M002','일화'),('M003','인증샷'),('M004','집결 장소'),('U001','학생'),('U002','교사');
/*!40000 ALTER TABLE `common_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `heritage_books`
--

DROP TABLE IF EXISTS `heritage_books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `heritage_books` (
  `user_id` bigint NOT NULL,
  `card_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`card_id`),
  KEY `card_id` (`card_id`),
  CONSTRAINT `heritage_books_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `heritage_books_ibfk_2` FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `heritage_books`
--

LOCK TABLES `heritage_books` WRITE;
/*!40000 ALTER TABLE `heritage_books` DISABLE KEYS */;
/*!40000 ALTER TABLE `heritage_books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `heritage_places`
--

DROP TABLE IF EXISTS `heritage_places`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `heritage_places` (
  `place_id` bigint NOT NULL AUTO_INCREMENT,
  `place_name` varchar(100) NOT NULL,
  PRIMARY KEY (`place_id`),
  KEY `idx_place_name` (`place_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `heritage_places`
--

LOCK TABLES `heritage_places` WRITE;
/*!40000 ALTER TABLE `heritage_places` DISABLE KEYS */;
INSERT INTO `heritage_places` VALUES (1,'경복궁'),(2,'인동향교');
/*!40000 ALTER TABLE `heritage_places` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `heritage_problems`
--

DROP TABLE IF EXISTS `heritage_problems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `heritage_problems` (
  `problem_id` bigint NOT NULL AUTO_INCREMENT,
  `card_id` bigint NOT NULL,
  `heritage_name` varchar(100) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `description` text,
  `content` text NOT NULL,
  `object_image_url` varchar(255) NOT NULL,
  `example1` varchar(50) NOT NULL,
  `example2` varchar(50) NOT NULL,
  `example3` varchar(50) NOT NULL,
  `example4` varchar(50) NOT NULL,
  `answer` varchar(50) NOT NULL,
  PRIMARY KEY (`problem_id`),
  KEY `card_id` (`card_id`),
  CONSTRAINT `heritage_problems_ibfk_1` FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `heritage_problems`
--

LOCK TABLES `heritage_problems` WRITE;
/*!40000 ALTER TABLE `heritage_problems` DISABLE KEYS */;
INSERT INTO `heritage_problems` VALUES (1,3,'사정전','heritage/problems/91ee2fc3-2a2d-462c-a2d2-21c2ff9c70bb_image (20).png','경복궁의 왕의 일상 정무 공간. 왕의 집무실이자 신하들과의 소규모 회의가 이루어진 곳으로, 근정전보다 격식이 덜한 공간이었다. 왕이 평상시에 정무를 보던 곳이며, 궁중 연회나 과거 시험장으로도 사용되었다.','저는 경복궁의 편전이에요! 임금님이 신하들과 함께 회의를 하던 곳이에요. 저는 무엇일까요?','heritage/objects/cfb5b2fe-fa24-4b57-b7dc-dadd83347a98_gyeongbokgung_geunjeongjeon_royalseal-Photoroom.png','사진전','강녕전','수정전','사정전','사정전'),(2,1,'근정전','heritage/problems/1e06e2f1-7253-45e8-a862-daf9d90bb558_image (21).png','복궁의 정전으로 조선왕조의 법궁인 경복궁에서 가장 격식 있는 건물. 즉위식, 조회, 외국 사신 접견 등 국가의 주요 의식이 거행되던 곳이다. 2층 월대를 갖춘 단층 건물로, 전면에는 품계석이 있어 신하들의 위치를 정했다.','저는 경복궁의 정전이에요! 임금님이 공식적으로 나라의 중요한 일을 처리하던 곳이에요. 저는 무엇일까요?','heritage/objects/055c02db-5b86-417e-b8a0-ac1174aab3f2_gyeongbokgung_sajeongjeon_ilwolobongdo-Photoroom.png','경회루','사정전','근정전','강녕전','근정전'),(3,2,'경회루','heritage/problems/cc270829-8ae1-409c-b1bb-e21fe799d4d9_image (23).png','복궁의 연못 위에 지어진 육각형 누각으로 연회와 외국 사신 접대 등에 사용된 건물. 272개의 돌기둥이 건물을 떠받치고 있으며, 연못에는 작은 섬들이 조성되어 있다.','저는 경복궁의 누각이에요! 임금님이 연회를 베풀거나 사신을 맞이하던 곳이에요. 저는 무엇일까요?','heritage/objects/2732dad1-a028-4b27-b58b-d91f6e791187_gyeongbokgung_cheonchujeon_waterdropper-Photoroom.png','근정전','경회루','사정전','강녕전','경회루'),(4,4,'인동향교','heritage/problems/b58180d7-39cb-47c0-b3d5-b872d8d4536d_Indonghyanggyo.png','경상북도 구미시에 있는 조선후기에 중건된 향교 및 교육시설로, 조선 중기에 현유의 위패를 봉안, 배향하고 지방민의 교육과 교화를 위하여 창건되었다. 창건연대는 미상이며, 현재의 향교는 1864년(고종 1)에 중건된 것이다. 고종 말기에는 인동국민학교의 전신인 옥성학원이 설립되어 명륜당을 교사로 쓰기도 하였다. 동무와 서무는 6·25 때 소실되었고 동재와 서재도 그 뒤 철거되어 서재 자리에 관리사를 지었다. 현존하는 건물로는 6칸의 대성전, 8칸의 명륜당, 신도문과 외삼문 등이 있다. 명륜당은 초익공오량가이며 공자형 지붕을 하고 있어 명륜당 건물로서는 특이한 형태를 이루고 있다.','저는 경북구미의 소중한 문화재예요! 조선시대에 만들어진 학교였는데, 그때는 선생님 1명이 30명의 학생들을 가르쳤다고 해요. 지금은 6칸의 큰 건물에서 공자님과 여러 훌륭한 분들을 모시고, 봄과 가을에 제사를 지내요. 저는 무엇일까요???','heritage/objects/1f24299b-d40b-4b17-88c1-438c41260b06_incenseburner.png','인동향교','인동서원','인동학당','인동사찰','인동향교');
/*!40000 ALTER TABLE `heritage_problems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logs`
--

DROP TABLE IF EXISTS `logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logs` (
  `user_id` bigint NOT NULL,
  `card_id` bigint NOT NULL,
  `result` tinyint(1) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`card_id`),
  KEY `logs_ibfk_2` (`card_id`),
  CONSTRAINT `logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `logs_ibfk_2` FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logs`
--

LOCK TABLES `logs` WRITE;
/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `memberships`
--

DROP TABLE IF EXISTS `memberships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `memberships` (
  `user_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `code_id` varchar(10) NOT NULL,
  `group_no` int NOT NULL,
  PRIMARY KEY (`user_id`,`room_id`),
  KEY `room_id` (`room_id`),
  KEY `code_id` (`code_id`),
  CONSTRAINT `memberships_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `memberships_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
  CONSTRAINT `memberships_ibfk_3` FOREIGN KEY (`code_id`) REFERENCES `common_codes` (`code_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `memberships`
--

LOCK TABLES `memberships` WRITE;
/*!40000 ALTER TABLE `memberships` DISABLE KEYS */;
/*!40000 ALTER TABLE `memberships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mission_positions`
--

DROP TABLE IF EXISTS `mission_positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mission_positions` (
  `mission_id` bigint NOT NULL AUTO_INCREMENT,
  `place_id` bigint NOT NULL,
  `code_id` varchar(10) NOT NULL,
  `card_id` bigint DEFAULT NULL,
  `position_name` varchar(100) DEFAULT NULL,
  `center_point` point NOT NULL,
  `edge_points` polygon NOT NULL,
  PRIMARY KEY (`mission_id`),
  KEY `place_id` (`place_id`),
  KEY `code_id` (`code_id`),
  KEY `card_id` (`card_id`),
  CONSTRAINT `mission_positions_ibfk_1` FOREIGN KEY (`place_id`) REFERENCES `heritage_places` (`place_id`),
  CONSTRAINT `mission_positions_ibfk_2` FOREIGN KEY (`code_id`) REFERENCES `common_codes` (`code_id`),
  CONSTRAINT `mission_positions_ibfk_3` FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mission_positions`
--

LOCK TABLES `mission_positions` WRITE;
/*!40000 ALTER TABLE `mission_positions` DISABLE KEYS */;
INSERT INTO `mission_positions` VALUES (11,1,'M001',3,'삼성1',_binary '\0\0\0\0\0\0\0��cO\r`@\�b\0X�\rB@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\�BS�N\r`@�\�e�\rB@q\�P\r`@�\�e�\rB@q\�P\r`@\r\��J�\rB@\�BS�N\r`@\r\��J�\rB@\�BS�N\r`@�\�e�\rB@'),(12,1,'M002',NULL,'삼성2',_binary '\0\0\0\0\0\0\0���S\r`@s��\rB@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0�5]�R\r`@4{#�\rB@��\�kT\r`@4{#�\rB@��\�kT\r`@��\�\rB@�5]�R\r`@��\�\rB@�5]�R\r`@4{#�\rB@'),(13,1,'M003',NULL,'인증샷 미션(조장만 참여가능)',_binary '\0\0\0\0\0\0\0�o\�IQ\r`@�z�U�\rB@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\�>s\�P\r`@�%ܩ\rB@��1�Q\r`@�%ܩ\rB@��1�Q\r`@C>Ϭ\rB@\�>s\�P\r`@C>Ϭ\rB@\�>s\�P\r`@�%ܩ\rB@'),(14,2,'M001',4,'인동향교',_binary '\0\0\0\0\0\0\0E|\�`@~�$AB@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0�\�U�\�`@\�oBB@�q7�\�`@\�oBB@�o\�`@��U�@B@\�I�\�`@�\�\�|@B@�\�U�\�`@\�oBB@'),(15,2,'M003',NULL,'인증샷 미션(조장만 참여가능)',_binary '\0\0\0\0\0\0\0]QJ\�`@.��\'HB@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0Χ�\�`@�n�HB@\��}\�`@7R�HB@\�7�{\�`@#�GGB@��ْ\�`@���#GB@Χ�\�`@�n�HB@'),(16,1,'M001',1,'남경비앙채 옆',_binary '\0\0\0\0\0\0\0G!ɬ^\r`@M����\rB@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\�\�C\�]\r`@�\�k�\rB@\��\�q_\r`@����\rB@�8�~_\r`@\�t \�\rB@m\�\�]\r`@\�t \�\rB@\�\�C\�]\r`@�\�k�\rB@'),(18,1,'M001',7,'경회루',_binary '\0\0\0\0\0\0\0�A\��v�_@8�{\�5\�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0ݙ	�s�_@\Zl\�<\�B@�Q}�_@j\'�;\�B@7P\��|�_@q\r\�-\�B@xԘs�_@\"R\�.\�B@ݙ	�s�_@\Zl\�<\�B@'),(19,1,'M001',2,'근정전',_binary '\0\0\0\0\0\0\0�\�l��_@�Z\�Q\�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0gs��_@fg\�;\�B@\0�&��_@\rP\Zj\�B@�ʢ���_@{��\�B@�xxρ�_@\�1˞\�B@gs��_@fg\�;\�B@'),(20,1,'M001',4,'사정전',_binary '\0\0\0\0\0\0\0yv�և�_@mu9% \�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0?�7j��_@__\�R#\�B@6׆��_@__\�R#\�B@<��X��_@\�ֈ`\�B@\�\�\��_@\���\�B@?�7j��_@__\�R#\�B@'),(21,1,'M002',NULL,NULL,_binary '\0\0\0\0\0\0\0\�q�d�_@T�\�\�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0�1�Mc�_@\�c\�C\�B@���e�_@�	j�\�B@O>=�e�_@�\'�\�B@>!;oc�_@A��\�B@�1�Mc�_@\�c\�C\�B@'),(22,1,'M002',NULL,NULL,_binary '\0\0\0\0\0\0\0�#\�w~�_@\�b�0\�\�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0�҈�}�_@ߨ�\�\�B@Ϡ��_@ߨ�\�\�B@_���_@ɐc\�\�B@1_^�}�_@R��\�\�B@�҈�}�_@ߨ�\�\�B@'),(23,1,'M002',NULL,NULL,_binary '\0\0\0\0\0\0\0�\�Fu:�_@�\�[X7\�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0�Z��_@��9\�B@E�k맾_@��9\�B@ԀAҧ�_@g&\�5\�B@^�)ʥ�_@���6\�B@�Z��_@��9\�B@'),(24,1,'M002',NULL,NULL,_binary '\0\0\0\0\0\0\0bi\�G5�_@��ݯ\�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0Oϻ���_@:�w\�\�B@衶\r��_@:�w\�\�B@\�x�ߢ�_@�X�O\�B@�B\�ʠ�_@��ѫ\�B@Oϻ���_@:�w\�\�B@'),(33,1,'M003',NULL,'인증샷 미션(조장만 참여가능)',_binary '\0\0\0\0\0\0\0�X򠖾_@Z�\�QJ\�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0\�\���_@N�\�\�P\�B@\�[����_@�CQ�O\�B@f����_@4�\�<\�B@g(\�x��_@.��\�=\�B@\�\���_@N�\�\�P\�B@'),(34,1,'M003',NULL,'인증샷 미션(조장만 참여가능)',_binary '\0\0\0\0\0\0\0\�]����_@\��\�H\�B@',_binary '\0\0\0\0\0\0\0\0\0\0\0\0\0/O犾_@J{�/L\�B@v�+.��_@J{�/L\�B@v�+.��_@��ʡE\�B@/O犾_@��ʡE\�B@/O犾_@J{�/L\�B@');
/*!40000 ALTER TABLE `mission_positions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pictures`
--

DROP TABLE IF EXISTS `pictures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pictures` (
  `picture_id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `mission_id` bigint NOT NULL,
  `picture_url` varchar(255) NOT NULL,
  `completion_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`picture_id`),
  KEY `room_id` (`room_id`),
  KEY `user_id` (`user_id`),
  KEY `mission_id` (`mission_id`),
  CONSTRAINT `pictures_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
  CONSTRAINT `pictures_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `pictures_ibfk_3` FOREIGN KEY (`mission_id`) REFERENCES `mission_positions` (`mission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pictures`
--

LOCK TABLES `pictures` WRITE;
/*!40000 ALTER TABLE `pictures` DISABLE KEYS */;
/*!40000 ALTER TABLE `pictures` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `quiz_types`
--

DROP TABLE IF EXISTS `quiz_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quiz_types` (
  `quiz_id` bigint NOT NULL AUTO_INCREMENT,
  `problem_id` bigint NOT NULL,
  `content` text NOT NULL,
  `initial` varchar(100) NOT NULL,
  `answer` varchar(100) NOT NULL,
  PRIMARY KEY (`quiz_id`),
  KEY `problem_id` (`problem_id`),
  CONSTRAINT `quiz_types_ibfk_1` FOREIGN KEY (`problem_id`) REFERENCES `story_problems` (`problem_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `quiz_types`
--

LOCK TABLES `quiz_types` WRITE;
/*!40000 ALTER TABLE `quiz_types` DISABLE KEYS */;
INSERT INTO `quiz_types` VALUES (1,3,'OOO의 부서진 어좌|||OOO은 선조의 아들이다. OOO은 연산군과 같이 묘호를 받지 못한 유이한 왕이다.','ㄱㅎㄱ','광해군'),(2,3,'광해군의 부서진 OO|||광해군은 선조의 아들이다. 광해군은 연산군과 같이 묘호를 받지 못한 유이한 왕이다.','ㅇㅈ','어좌'),(3,1,'사도세자의 눈물 젖은 OO|||사도세자는 영조의 둘째 아들이다. 그는 아버지의 명으로 OO에 갇혀 8일 만에 죽었다.','ㄷㅈ','뒤주'),(4,1,'OOOO의 눈물 젖은 뒤주|||OOOO는 영조의 둘째 아들이다. 그는 아버지의 명으로 뒤주에 갇혀 8일 만에 죽었다.','ㅅㄷㅅㅈ','사도세자'),(5,2,'정몽주의 운명이 걸린 OOO|||정몽주는 고려 말의 충신이다. 이방원에 의해 OOO에서 살해되었다.','ㅅㅈㄱ','선죽교'),(6,2,'OOO의 운명이 걸린 선죽교|||OOO는 고려 말의 충신이다. 이방원에 의해 선죽교에서 살해되었다.','ㅈㅁㅈ','정몽주'),(7,4,'관창의 주인 잃은 O|||관창은 고구려의 장수이다. 을지문덕의 살수대첩에서 O이 죽어 전사했다.','ㅁ','말'),(8,4,'OO의 주인 잃은 말|||OO은 고구려의 장수이다. 을지문덕의 살수대첩에서 말이 죽어 전사했다.','ㄱㅊ','관창'),(9,5,'안중근의 녹슨 OO|||안중근은 대한제국의 독립운동가이다. 하얼빈에서 이토 히로부미를 저격했다.','ㄱㅊ','권총'),(10,5,'OOO의 녹슨 권총|||OOO은 대한제국의 독립운동가이다. 하얼빈에서 이토 히로부미를 저격했다.','ㅇㅈㄱ','안중근'),(11,6,'이방원의 차갑게 식어버린 OO(사용 후)|||이방원은 태종이 되기 전 정몽주를 선죽교에서 OO로 살해했다.','ㅊㅌ','철퇴'),(12,6,'OOO의 차갑게 식어버린 철퇴(사용 후)|||OOO은 태종이 되기 전 정몽주를 선죽교에서 철퇴로 살해했다.','ㅇㅂㅇ','이방원'),(13,10,'세조의 자존심을 흔든 OO|||세조는 조선의 7대 왕이다. 그는 음악을 좋아해 특히 OO 연주를 즐겼다.','ㅌㅅ','통소'),(14,10,'OO의 자존심을 흔든 통소|||OO는 조선의 7대 왕이다. 그는 음악을 좋아해 특히 통소 연주를 즐겼다.','ㅅㅈ','세조'),(15,7,'정조의 관심받은 OOO|||정조는 조선의 22대 왕이다. OOO 피해를 줄이기 위해 백성들에게 포상을 내렸다.','ㅅㅊㅇ','송충이'),(16,7,'OO의 관심받은 송충이|||OO는 조선의 22대 왕이다. 송충이 피해를 줄이기 위해 백성들에게 포상을 내렸다.','ㅈㅈ','정조'),(17,8,'흥선대원군의 완고한 OOO|||흥선대원군은 고종의 아버지이다. 서양세력 배척을 위해 전국에 OOO를 세웠다.','ㅊㅎㅂ','척화비'),(18,8,'OOOOO의 완고한 척화비|||OOOOO은 고종의 아버지이다. 서양세력 배척을 위해 전국에 척화비를 세웠다.','ㅎㅅㄷㅇㄱ','흥선대원군'),(19,9,'경희루의 굳건한 OOO|||경회루는 경복궁의 연못 위에 지은 누각이다. 272개의 OOO이 받치고 있다.','ㄷㄱㄷ','돌기둥'),(20,9,'OOO의 굳건한 돌기둥|||OOO는 경복궁의 연못 위에 지은 누각이다. 272개의 돌기둥이 받치고 있다.','ㄱㅎㄹ','경회루');
/*!40000 ALTER TABLE `quiz_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rally_points`
--

DROP TABLE IF EXISTS `rally_points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rally_points` (
  `user_id` bigint NOT NULL,
  `code_id` varchar(10) NOT NULL,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  PRIMARY KEY (`user_id`),
  KEY `code_id` (`code_id`),
  CONSTRAINT `rally_points_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `rally_points_ibfk_2` FOREIGN KEY (`code_id`) REFERENCES `common_codes` (`code_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rally_points`
--

LOCK TABLES `rally_points` WRITE;
/*!40000 ALTER TABLE `rally_points` DISABLE KEYS */;
/*!40000 ALTER TABLE `rally_points` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `room_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `no1` int DEFAULT NULL,
  `no2` int DEFAULT NULL,
  `no3` int DEFAULT NULL,
  `no4` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`room_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`),
  CONSTRAINT `reports_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rooms`
--

DROP TABLE IF EXISTS `rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rooms` (
  `room_id` bigint NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint NOT NULL,
  `location` varchar(20) DEFAULT NULL,
  `room_name` varchar(50) NOT NULL,
  `num_of_groups` int NOT NULL,
  `invite_code` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`room_id`),
  UNIQUE KEY `idx_unique_invite_code` (`invite_code`),
  KEY `teacher_id` (`teacher_id`),
  CONSTRAINT `rooms_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rooms`
--

LOCK TABLES `rooms` WRITE;
/*!40000 ALTER TABLE `rooms` DISABLE KEYS */;
/*!40000 ALTER TABLE `rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedules`
--

DROP TABLE IF EXISTS `schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedules` (
  `schedule_id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `start_time` timestamp NOT NULL,
  `content` varchar(255) NOT NULL,
  `end_time` timestamp NOT NULL,
  PRIMARY KEY (`schedule_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `schedules_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedules`
--

LOCK TABLES `schedules` WRITE;
/*!40000 ALTER TABLE `schedules` DISABLE KEYS */;
/*!40000 ALTER TABLE `schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `social_accounts`
--

DROP TABLE IF EXISTS `social_accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `social_accounts` (
  `user_id` bigint NOT NULL,
  `provider_id` varchar(255) NOT NULL,
  `refresh_token` text NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `provider_id` (`provider_id`),
  CONSTRAINT `social_accounts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `social_accounts`
--

LOCK TABLES `social_accounts` WRITE;
/*!40000 ALTER TABLE `social_accounts` DISABLE KEYS */;
INSERT INTO `social_accounts` VALUES (1,'naver8Yp-YolB8yUi9TyWsNrSWWwtnigrz0CX5iuliSqHowY','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlcjhZcC1Zb2xCOHlVaTlUeVdzTnJTV1d3dG5pZ3J6MENYNWl1bGlTcUhvd1kiLCJpYXQiOjE3Mzk3NTIyMTIsImV4cCI6MTc0MDk2MTgxMn0.BJHfcNm22byFIvlz9shSofsbh49R2lvfIZ4r1N6RlhBNQ_d5WawLhd7HkxcgP95lQ4xmX1mMPLtKbd8SnpHXXw'),(2,'naverk2kkgkFhs5AdyX90CCRYbECd3DGzKGwZLAUhkiBZ-iU','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlcmsya2tna0ZoczVBZHlYOTBDQ1JZYkVDZDNER3pLR3daTEFVaGtpQlotaVUiLCJpYXQiOjE3Mzk5MjU5MDcsImV4cCI6MTc0MTEzNTUwN30.gzWF8lvTpaIy0XyqJ6408SuY4DFMvG7TyNl8xA7UPF8Xmq5EixB6a4f9Hl2xXwlcV20gWBN0mIMJZBjL76jGNw'),(3,'naverJj0EdYDWLszSjaPXSOKnSl82wARQODKyREG5U50DHSA','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlckpqMEVkWURXTHN6U2phUFhTT0tuU2w4MndBUlFPREt5UkVHNVU1MERIU0EiLCJpYXQiOjE3Mzk5MjkwMDQsImV4cCI6MTc0MTEzODYwNH0.uFayXkOfYBKWQSNlBVjYASAKV3VxRofKlTnShigvW2-w8MOBykTfmmrG9n6_ggbEKo-_p6VjxuqHqtMBhJ3INw'),(4,'kakao3909085270','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MDkwODUyNzAiLCJpYXQiOjE3Mzk3NTMxOTAsImV4cCI6MTc0MDk2Mjc5MH0.RNrZk5Ep3l_k8GIpWyeiwMD9mWo9n_6ecb4eWwyQLfFmy6xGW_0hYOolYYGxeae0q0pr-IPx2btGBU8wM7UJyA'),(5,'kakao3909727572','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MDk3Mjc1NzIiLCJpYXQiOjE3Mzk3NTM2NzAsImV4cCI6MTc0MDk2MzI3MH0.z0ree6v0-6otV0oon0C5yjKep04lBCM9bEzaRgRmb7HdJiLkdC1QOmdWykYwSxvHoffj4qLbbbDkX4N7eheLVQ'),(6,'naverQTeS0OCbCYHtNMkQOqM8rZON2hoQNkOVK93DMVMeUB4','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlclFUZVMwT0NiQ1lIdE5Na1FPcU04clpPTjJob1FOa09WSzkzRE1WTWVVQjQiLCJpYXQiOjE3Mzk5MzE0ODUsImV4cCI6MTc0MTE0MTA4NX0.JM2pUGMg2pZGxtD_ovc8MpTJMbXb7Io9NlIFhSbz76-fkd2g9lUH0SFOHopMBn0LWUvFrjjrjUbLQug26jyKCA'),(7,'kakao3922256453','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MjIyNTY0NTMiLCJpYXQiOjE3Mzk5MzYzMjQsImV4cCI6MTc0MTE0NTkyNH0.QV1OCKyWvTaQa0yW8T0Bz7eXupGnxI3LJhWpfQ1Idyb5QskRSkp7q6nz4Yns5M6yPXYjXfLFbvt_NwRBXG9pGg'),(8,'kakao3922003715','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MjIwMDM3MTUiLCJpYXQiOjE3Mzk3ODEzMzgsImV4cCI6MTc0MDk5MDkzOH0.hRDxPm0kfisMzmP6HDZPP-Hk55KskAiCEQpemdS22yO2XzvJR1aj00_VP_ZOWP_f8Q3aPHrbXxSoQzWsnXgzzQ'),(9,'kakao3926213893','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MjYyMTM4OTMiLCJpYXQiOjE3Mzk4NDM4NDUsImV4cCI6MTc0MTA1MzQ0NX0.IVV8Dy8j2JAcPmVaN2ktStKVe1V1DJpoAPnLd0RhexiIVNb63PXTzlLBs5-2NKK_JIfHU3YaiA-9sUXUAiPupQ'),(10,'kakao3926217812','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MjYyMTc4MTIiLCJpYXQiOjE3Mzk4NDQwMDcsImV4cCI6MTc0MTA1MzYwN30.xlRSF6wPNJ3cHEUtivXEm9foIPuGVG8iCVZ2w6xA7PSSuxGZFLnVRJjOgH09EtlolQB0svXh5wDLE6BLpEaNHg'),(11,'kakao3907193195','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MDcxOTMxOTUiLCJpYXQiOjE3Mzk5NDIzMTAsImV4cCI6MTc0MTE1MTkxMH0.hOaqPkfap81MbSr23pB7ItiaoEwNt_XozY9bTucGWcFQeHpA0XJwsciWl0cz8Erwu1MbiuF2AKyScUegmiiS0w'),(12,'kakao3926320618','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MjYzMjA2MTgiLCJpYXQiOjE3Mzk5NDQ3NzYsImV4cCI6MTc0MTE1NDM3Nn0.cHGAApSrDBQRpxuMKfbnDQmlsO6lPrfxcSY_HlCzKwQpBWnIckWhD96O-aSrqYREzhEtbMUtCj2mrrVz6_mzeA'),(13,'naverO-_ZL8JwaY2IPci2h7C7J3xUgDrmUe5KTYUr7oy0KeI','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlck8tX1pMOEp3YVkySVBjaTJoN0M3SjN4VWdEcm1VZTVLVFlVcjdveTBLZUkiLCJpYXQiOjE3Mzk5MzU0MTMsImV4cCI6MTc0MTE0NTAxM30.W4jfVi7tr5hSJ4GZo002xjAdwDzO6knKV6Y6jmv-Cm4gR1QyS4bKEHwDa8vRuwBorGemHtGcVe8aK5_jJhHVZg'),(14,'kakao3908663678','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthbzM5MDg2NjM2NzgiLCJpYXQiOjE3NDAwMTU5OTgsImV4cCI6MTc0MTIyNTU5OH0.F9pBhuWimYpLZcjG9WfT8gnoo4qRqx_rWXirEB1nDMIJPx-isnmjZ29RKNbOey2eayvfiWlnstV0r_27Y31Wmg'),(15,'naverpsKQ2Yhps50WRYKY1iHlJ-rX7nb3MOweFk9cprtyTyw','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlcnBzS1EyWWhwczUwV1JZS1kxaUhsSi1yWDduYjNNT3dlRms5Y3BydHlUeXciLCJpYXQiOjE3NDAwNDczNzAsImV4cCI6MTc0MTI1Njk3MH0.TgOKLViLhqVN3PZYjX1dB3E7soZ83ykWk2WLRIa-MQVFceuE7MTPoWAiTmzw5wsbfoSPUOENbnBjAtf0qWkQsA'),(16,'naverUv_664UGv39H5Ie_5_tKo_LA5mSzbxUu0UhsUfFFZBE','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlclV2XzY2NFVHdjM5SDVJZV81X3RLb19MQTVtU3pieFV1MFVoc1VmRkZaQkUiLCJpYXQiOjE3Mzk5Mzk2NDIsImV4cCI6MTc0MTE0OTI0Mn0.rP40AA_u_DoHHjSNRkQ1AwgIp-xOgl70iCR5Q1ip45UXQK-mBzCAm570nSodPhnwORXv5k1D-t1FTNSIRdNgGw'),(17,'naverN7m4P36Y3x9R4U-IJNKNUW1iv1CEutc1_R7mECN2rhc','eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlck43bTRQMzZZM3g5UjRVLUlKTktOVVcxaXYxQ0V1dGMxX1I3bUVDTjJyaGMiLCJpYXQiOjE3NDAwMTQyMTEsImV4cCI6MTc0MTIyMzgxMX0.yAgg2OtcDMXDQtTGhUfGDHeV_OjhIaxYADxZV5adRNjNjQ5UyO6UmFI0KSN_e6PVuwDw5yqmL1rEo5_X90HCVw');
/*!40000 ALTER TABLE `social_accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `story_problems`
--

DROP TABLE IF EXISTS `story_problems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `story_problems` (
  `problem_id` bigint NOT NULL AUTO_INCREMENT,
  `card_id` bigint NOT NULL,
  `object_name` varchar(100) DEFAULT NULL,
  `description` text,
  `content` text NOT NULL,
  `black_icon_image_url` varchar(255) DEFAULT NULL,
  `color_icon_image_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`problem_id`),
  KEY `card_id` (`card_id`),
  CONSTRAINT `story_problems_ibfk_1` FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `story_problems`
--

LOCK TABLES `story_problems` WRITE;
/*!40000 ALTER TABLE `story_problems` DISABLE KEYS */;
INSERT INTO `story_problems` VALUES (1,11,'뒤주','뒤주는 쌀 등의 곡식을 담아 두는 나무로 만든 궤로 세간의 하나다. 사도세자 가둔 상자\'로 인식되어 사도세자를 \'뒤주세자\' 라고 부르거나 혹은 임오화변을 뒤주 대첩이라 칭한다.','사도세자의 눈물 젖은 뒤주|||사도세자는 영조의 둘째 아들이다. 그는 아버지의 명으로 뒤주에 갇혀 8일 만에 죽었다.','story/problems/76c5ba8d-e138-413c-8606-cfab2f6d4cb3_bicon1.png','story/problems/419b64af-5806-4a6f-b09b-150f7e8f2b1c_icon1.png'),(2,13,'선죽교','선죽교는 처음에 선지교였으나 고려말 정몽주선생이 신흥 조선왕조에 협력하지 않는다고 하여 이방원의 일파였던 조영규에 의해 비극적인 죽음을 맞이한 곳이다. 그때 흘린 선혈이 돌다리 아래로 흘러내려 개울에서 대나무가 솟아올랐다고 하여 후에 선죽교로 고쳐 부른 것이다.','정몽주의 운명이 걸린 선죽교|||정몽주는 고려 말의 충신이다. 이방원에 의해 선죽교에서 살해되었다.','story/problems/72562ad4-6eda-4ff7-b024-41786dfce44a_bicon3.png','story/problems/b34d49bf-e195-4817-b23e-3e39eb039854_icon3.png'),(3,12,'어좌','임금이 앉는 자리 또는 의자를 뜻하는 용어. 광해군이 폐위될 때 그의 어좌가 부서진 사건은 왕권의 몰락을 상징적으로 보여준다.','광해군의 부서진 어좌|||광해군은 선조의 아들이다. 광해군은 연산군과 같이 묘호를 받지 못한 유이한 왕이다.','story/problems/782b8f82-c1eb-4539-9ffe-6a5df7b23153_bicon2.png','story/problems/b0bf5dd1-d3f1-46a3-8833-93740ff1a707_icon2.png'),(4,14,'말','살수대첩 당시 고구려 장수 관창이 탔던 말. 전투 중 말이 전사하면서 관창도 함께 전사했다는 기록이 전해진다.','관창의 주인 잃은 말|||관창은 고구려의 장수이다. 을지문덕의 살수대첩에서 말이 죽어 전사했다.','story/problems/ab23d5b2-3734-47f8-b06b-621d5313d6d7_bicon4.png','story/problems/ea29285d-ce16-4b5f-94e5-f1448ca097b2_icon4.png'),(5,15,'권총','1909년 안중근 의사가 하얼빈 역에서 이토 히로부미를 저격할 때 사용한 무기. 브라우닝 M1900 권총이었다.','안중근의 녹슨 권총|||안중근은 대한제국의 독립운동가이다. 하얼빈에서 이토 히로부미를 저격했다.','story/problems/240039d9-25a9-4ae5-bab0-50f776dd1794_bicon5.png','story/problems/3a58ec31-95d5-4bb2-b616-cfad6f07380f_icon5.png'),(6,16,'철퇴','쇠로 만든 무기로, 이방원(후의 태종)이 정몽주를 살해할 때 사용했다고 전해진다. 긴 자루 끝에 쇠로 된 둥근 머리가 달린 형태의 타격용 무기이다.','이방원의 차갑게 식어버린 철퇴(사용 후)|||이방원은 태종이 되기 전 정몽주를 선죽교에서 철퇴로 살해했다.','story/problems/0e5e29ff-9f19-430f-95bb-1f1210e98629_bicon6.png','story/problems/f7d6438f-6b00-428c-bac9-5c6bea8d3649_icon6.png'),(7,18,'송충이','소나무에 피해를 주는 해충. 정조는 송충이 구제 사업을 벌여 이를 잡아오는 백성들에게 상을 내렸다.','정조의 관심받은 송충이|||정조는 조선의 22대 왕이다. 송충이 피해를 줄이기 위해 백성들에게 포상을 내렸다.','story/problems/d73b5a21-ddb4-4ab2-8803-48a93e90a791_bicon8.png','story/problems/c8ab9c23-6fd0-4a2f-b128-eca0952d5ba3_icon8.png'),(8,19,'척화비','흥선대원군이 1871년 전국 각지에 세운 반외세 비석. \"서양 오랑캐와 통하면 나라가 망한다\"는 내용을 담고 있다.','흥선대원군의 완고한 척화비|||흥선대원군은 고종의 아버지이다. 서양세력 배척을 위해 전국에 척화비를 세웠다.','story/problems/490d06d9-03e6-4a53-935d-615f81cb7a46_bicon9.png','story/problems/fa52b326-8525-41ae-ada6-d71166952ca8_icon9.png'),(9,20,'돌기둥','경복궁 경회루를 떠받치고 있는 272개의 석조 기둥. 연못 위에 지어진 경회루의 구조적 토대를 이루며, 조선 건축 기술의 우수성을 보여주는 증거이다.','경희루의 굳건한 돌기둥|||경회루는 경복궁의 연못 위에 지은 누각이다. 272개의 돌기둥이 받치고 있다.','story/problems/30ce8d82-b588-4884-91e1-9bfe8f60370d_bicon10.png','story/problems/3cf8b5d7-2de3-476e-8ef5-2d6315d0ed71_icon10.png'),(10,17,'통소','세로로 부는 대나무 관악기. 세조가 특히 즐겨 연주했다고 전해지며, 이는 그의 예술적 취향을 보여주는 예시로 자주 언급된다.','세조의 자존심을 흔든 통소|||세조는 조선의 7대 왕이다. 그는 음악을 좋아해 특히 통소 연주를 즐겼다.','story/problems/4fc87476-f499-4b2f-b66c-52f3d1dd46b2_bicon7.png','story/problems/29e10ad0-9e5f-4adb-a044-cca7fd28d660_icon7.png');
/*!40000 ALTER TABLE `story_problems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_notices`
--

DROP TABLE IF EXISTS `teacher_notices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_notices` (
  `notice_id` bigint NOT NULL AUTO_INCREMENT,
  `room_id` bigint NOT NULL,
  `content` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `title` varchar(100) DEFAULT NULL,
  `status` tinyint(1) NOT NULL,
  PRIMARY KEY (`notice_id`),
  KEY `room_id` (`room_id`),
  CONSTRAINT `teacher_notices_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_notices`
--

LOCK TABLES `teacher_notices` WRITE;
/*!40000 ALTER TABLE `teacher_notices` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher_notices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `today_cards`
--

DROP TABLE IF EXISTS `today_cards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `today_cards` (
  `today_card_id` bigint NOT NULL AUTO_INCREMENT,
  `card_id` bigint NOT NULL,
  `mission_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`today_card_id`),
  KEY `card_id` (`card_id`),
  KEY `mission_id` (`mission_id`),
  CONSTRAINT `today_cards_ibfk_1` FOREIGN KEY (`card_id`) REFERENCES `cards` (`card_id`),
  CONSTRAINT `today_cards_ibfk_2` FOREIGN KEY (`mission_id`) REFERENCES `mission_positions` (`mission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `today_cards`
--

LOCK TABLES `today_cards` WRITE;
/*!40000 ALTER TABLE `today_cards` DISABLE KEYS */;
INSERT INTO `today_cards` VALUES (8,14,12,'2025-02-18 15:00:00'),(9,20,12,'2025-02-19 15:00:00'),(10,11,22,'2025-02-20 10:17:17'),(11,17,21,'2025-02-20 15:00:00'),(12,18,12,'2025-02-20 15:00:00');
/*!40000 ALTER TABLE `today_cards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `code_id` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `nickname` varchar(40) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  KEY `code_id` (`code_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`code_id`) REFERENCES `common_codes` (`code_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'U001','박성민','박성민','sky1357123@naver.com',0,'2025-02-17 00:30:12'),(2,'U002','김병년','김봉년','qud5252@naver.com',0,'2025-02-19 00:45:07'),(3,'U001','이용재','이용재','dldydwo19l23@gmail.com',0,'2025-02-19 01:36:44'),(4,'U002','이용재','이용재','lyj3023@naver.com',0,'2025-02-17 00:46:30'),(5,'U002','박성민','박선생','sky1357123@naver.com',0,'2025-02-17 00:54:30'),(6,'U001','김병년','김병년','qud5252@naver.com',1,'2025-02-19 02:18:05'),(7,'U001','제갈민','제갈민','vlf5231@naver.com',0,'2025-02-19 03:38:44'),(8,'U001','은주','손은주','sonej94113@naver.com',0,'2025-02-17 08:35:38'),(9,'U002','제갈민','제갈센세','vlf5231@naver.com',0,'2025-02-18 01:57:25'),(10,'U002','은주','은주','sonej94113@naver.com',0,'2025-02-18 02:00:07'),(11,'U002','김병년','김병년','qud5252@naver.com',0,'2025-02-19 05:18:30'),(12,'U001','이용재','이용재2','lyj3023@naver.com',0,'2025-02-19 05:59:36'),(13,'U002','이용재','이용재','dldydwo19l23@gmail.com',0,'2025-02-19 03:23:33'),(14,'U001','김병년','압둘핫산','qud5252@naver.com',0,'2025-02-20 01:46:38'),(15,'U001','제갈민','제갈민','vlf5231@naver.com',0,'2025-02-20 10:29:30'),(16,'U002','박성민','박선생','sky1357123@naver.com',0,'2025-02-19 04:34:02'),(17,'U002','제갈민','제갈민','vlf5231@naver.com',0,'2025-02-20 01:16:51');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-21  9:44:03
