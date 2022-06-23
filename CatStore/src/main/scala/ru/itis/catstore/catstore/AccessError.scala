package ru.itis.catstore.catstore

import ru.itis.catstore.security.SecurityError

class AccessError extends SecurityError("Access denied")
