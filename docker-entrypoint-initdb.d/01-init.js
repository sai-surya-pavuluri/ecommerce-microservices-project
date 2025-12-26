// Switch to admin DB
db = db.getSiblingDB('admin');

// Create admin user (matches docker-compose env)
db.createUser({
    user: "admin",
    pwd: "admin",
    roles: [
        { role: "root", db: "admin" }
    ]
});

// Create application databases
db = db.getSiblingDB('customer');
db.createCollection('init');

db = db.getSiblingDB('notification');
db.createCollection('init');
