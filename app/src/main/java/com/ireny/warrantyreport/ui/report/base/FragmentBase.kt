package com.ireny.warrantyreport.ui.report.base

import androidx.fragment.app.Fragment
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.interfaces.IRefreshFragment
import com.ireny.warrantyreport.ui.report.interfaces.IUpdateFragment

abstract class FragmentBase: Fragment(),IRefreshFragment<Report>, IUpdateFragment<Report>