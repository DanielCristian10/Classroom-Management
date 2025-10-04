db = db.getSiblingDB("admin");

db.createUser({
    user: "adminUser",
    pwd: "strongPassword123",
    roles: [{ role: "userAdminAnyDatabase", db: "admin" }]
});

db = db.getSiblingDB("myDatabase");

db.createUser({
    user: "dbOwnerUser",
    pwd: "ownerPassword123",
    roles: [{ role: "dbOwner", db: "myDatabase" }]
});

db.createUser({
    user: "readWriteUser",
    pwd: "writePassword123",
    roles: [{ role: "readWrite", db: "myDatabase" }]
});
