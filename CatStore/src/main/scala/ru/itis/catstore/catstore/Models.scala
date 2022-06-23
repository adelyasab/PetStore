package ru.itis.catstore.catstore

case class Cat(id: Long, name: String, color: String, sold: Boolean, price: Float)

case class Order(id: Long, customer: Long, payed: Boolean, cat_id: Long)

case class Role(id: Long, user_id: Long, user_role: String)

class RoleValue(val value: String)

case class Admin() extends RoleValue("admin")
