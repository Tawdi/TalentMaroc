package io.github.tawdi.jobboard.company_offers.exceptions;

public class CompanyNotApprovedException extends RuntimeException {
    public CompanyNotApprovedException(String message) {
        super(message);
    }
}

