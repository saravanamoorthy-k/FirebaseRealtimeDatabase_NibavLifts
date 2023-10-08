package com.nibavlifts.firebaserealtimedatabase.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Employee {
    private var employeeId: String? = null
    private var employeeName: String? = null

    constructor()
    constructor(employeeId: String?, employeeName: String?) {
        this.employeeId = employeeId
        this.employeeName = employeeName
    }

    fun getEmployeeId(): String? {
        return employeeId
    }

    fun getEmployeeName(): String? {
        return employeeName
    }
}