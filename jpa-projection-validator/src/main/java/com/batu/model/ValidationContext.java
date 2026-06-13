package com.batu.model;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import com.batu.util.Reporter;

public record ValidationContext(
    Element entity,
    ProcessingEnvironment env,
    Reporter reporter
) {

}
