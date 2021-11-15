package com.silverpine.uu.sample.bluetooth.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silverpine.uu.sample.bluetooth.ui.Strings

class SectionHeaderViewModel(text: String = "", @StringRes textResourceId: Int = -1): ViewModel()
{
    constructor(@StringRes labelResourceId: Int): this("", labelResourceId)
    constructor(label: String): this(label, -1)

    private val _label = MutableLiveData<String?>(null)
    var label: LiveData<String?> = _label

    init
    {
        if (textResourceId != -1)
        {
            _label .value = Strings.load(textResourceId)
        }
        else
        {
            _label.value = text
        }
    }
}