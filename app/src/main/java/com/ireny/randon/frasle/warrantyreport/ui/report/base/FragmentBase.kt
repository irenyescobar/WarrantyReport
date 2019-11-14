package com.ireny.randon.frasle.warrantyreport.ui.report.base

import androidx.fragment.app.Fragment
import com.ireny.randon.frasle.warrantyreport.entites.Report
import com.ireny.randon.frasle.warrantyreport.ui.report.interfaces.IRefreshFragment
import com.ireny.randon.frasle.warrantyreport.ui.report.interfaces.IUpdateFragment

abstract class FragmentBase: Fragment(),IRefreshFragment<Report>, IUpdateFragment<Report>