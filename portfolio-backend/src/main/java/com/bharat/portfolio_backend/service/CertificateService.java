package com.bharat.portfolio_backend.service;

import com.bharat.portfolio_backend.entity.Certificate;
import com.bharat.portfolio_backend.enums.Visibility;
import com.bharat.portfolio_backend.exception.ResourceNotFoundException;
import com.bharat.portfolio_backend.repository.CertificateRepository;
import com.bharat.portfolio_backend.requestDto.CertificateRequest;
import com.bharat.portfolio_backend.responseDto.CertificateResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ProfileService profileService;
    private final TechnologyService technologyService;

    public CertificateService(CertificateRepository certificateRepository,
                              ProfileService profileService,
                              TechnologyService technologyService) {
        this.certificateRepository = certificateRepository;
        this.profileService = profileService;
        this.technologyService = technologyService;
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> findAll(boolean includeHidden) {
        List<Certificate> certificates = includeHidden
                ? certificateRepository.findAllByOrderByDisplayOrderAsc()
                : certificateRepository.findByVisibilityOrderByDisplayOrderAsc(Visibility.PUBLIC);
        return certificates.stream().map(CertificateService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CertificateResponse findById(Long id) {
        return toResponse(require(id));
    }

    @Transactional
    public CertificateResponse create(CertificateRequest request) {
        Certificate certificate = new Certificate();
        certificate.setProfile(profileService.requireProfile());
        apply(certificate, request);
        return toResponse(certificateRepository.save(certificate));
    }

    @Transactional
    public CertificateResponse update(Long id, CertificateRequest request) {
        Certificate certificate = require(id);
        apply(certificate, request);
        return toResponse(certificateRepository.save(certificate));
    }

    @Transactional
    public void delete(Long id) {
        certificateRepository.delete(require(id));
    }

    private void apply(Certificate certificate, CertificateRequest request) {
        certificate.setName(request.name().trim());
        certificate.setSourceName(request.sourceName().trim());
        certificate.setSourceUrl(TechnologyService.blankToNull(request.sourceUrl()));
        certificate.setDescription(request.description());
        certificate.setDownloadUrl(TechnologyService.blankToNull(request.downloadUrl()));
        certificate.setCredentialId(TechnologyService.blankToNull(request.credentialId()));
        certificate.setLogoUrl(TechnologyService.blankToNull(request.logoUrl()));
        certificate.setIssueDate(request.issueDate());
        certificate.setExpiryDate(request.expiryDate());
        certificate.setHasExpiry(Boolean.TRUE.equals(request.hasExpiry()));
        certificate.setVisibility(request.visibility() == null ? Visibility.PUBLIC : request.visibility());
        certificate.setFeatured(Boolean.TRUE.equals(request.featured()));
        certificate.setDisplayOrder(request.displayOrder() == null ? 0 : request.displayOrder());
        certificate.setTechnologies(technologyService.resolveByNames(request.technologies()));
    }

    private Certificate require(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Certificate", id));
    }

    static CertificateResponse toResponse(Certificate certificate) {
        return new CertificateResponse(
                certificate.getId(),
                certificate.getName(),
                certificate.getSourceName(),
                certificate.getSourceUrl(),
                certificate.getDescription(),
                certificate.getDownloadUrl(),
                certificate.getCredentialId(),
                certificate.getLogoUrl(),
                certificate.getIssueDate(),
                certificate.getExpiryDate(),
                certificate.isHasExpiry(),
                certificate.getVisibility(),
                certificate.isFeatured(),
                certificate.getDisplayOrder(),
                TechnologyService.toResponses(certificate.getTechnologies()));
    }
}