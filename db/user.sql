/*
 Navicat Premium Data Transfer

 Source Server         : mind-mqtt
 Source Server Type    : SQLite
 Source Server Version : 3017000
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3017000
 File Encoding         : 65001

 Date: 06/04/2022 16:22:24
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
  "user_id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "username" text(32),
  "password" text(32),
  "allow_pub" integer,
  "allow_sub" integer,
  "enable" integer,
  "created_by" integer,
  "created_date" integer,
  "update_by" integer,
  "update_date" integer
);

-- ----------------------------
-- Auto increment value for user
-- ----------------------------
UPDATE "sqlite_sequence" SET seq = 1 WHERE name = 'user';

PRAGMA foreign_keys = true;
