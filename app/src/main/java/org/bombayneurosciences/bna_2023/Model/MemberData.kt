package org.bombayneurosciences.bna_2023.Model

data class MemberData(
    val delegateId: Int,
    val title: String,
    val fname: String,
    val mname: String?,
    val lname: String,
    val userType: String,
    val membershipNo: String,
    val category: String,
    val mobileno: String,
    val altMobileno: String?,
    val email: String,
    val affiliation: String?,
    val instclinic: String?,
    val instclinicadd: String?,
    val country: String?,
    val state: String?,
    val city: String?,
    val pincode: String?,
    val createdAt: String
)