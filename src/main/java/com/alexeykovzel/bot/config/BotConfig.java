package com.alexeykovzel.bot.config;

public class BotConfig {
    private static final String RESOURCE_ARN = "arn:aws:rds:us-east-2:574318152798:cluster:polygbot-cluster";
    private static final String SECRET_ARN = "arn:aws:secretsmanager:us-east-2:574318152798:secret:RDSSecret-flIq4l3NCrdW-rEvOVb";
    private static final String DB_NAME = "polygbotDB";

    public static String getResourceArn() {
        return RESOURCE_ARN;
    }

    public static String getSecretArn() {
        return SECRET_ARN;
    }

    public static String getDBName() {
        return DB_NAME;
    }
}