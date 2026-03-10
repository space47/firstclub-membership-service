package com.firstclub.membership.modules.tier.application;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.firstclub.membership.common.exception.ResourceNotFoundException;
import com.firstclub.membership.common.mapper.DtoMapper;
import com.firstclub.membership.modules.tier.domain.entity.MembershipTier;
import com.firstclub.membership.modules.tier.domain.entity.TierBenefit;
import com.firstclub.membership.modules.tier.domain.entity.UserTier;
import com.firstclub.membership.modules.tier.domain.repository.MembershipTierRepository;
import com.firstclub.membership.modules.tier.domain.repository.TierBenefitRepository;
import com.firstclub.membership.modules.tier.domain.repository.UserTierRepository;
import com.firstclub.membership.modules.tier.dto.request.CreateTierBenefitRequest;
import com.firstclub.membership.modules.tier.dto.request.CreateTierRequest;
import com.firstclub.membership.modules.tier.dto.request.UpdateTierBenefitRequest;
import com.firstclub.membership.modules.tier.dto.request.UpdateTierRequest;
import com.firstclub.membership.modules.tier.dto.response.BenefitResponse;
import com.firstclub.membership.modules.tier.dto.response.TierResponse;
import com.firstclub.membership.modules.tier.dto.response.UserTierResponse;

@Service
@Transactional(readOnly = true)
public class TierService {
        private final MembershipTierRepository tierRepository;
        private final UserTierRepository userTierRepository;
        private final TierBenefitRepository tierBenefitRepository;

        public TierService(MembershipTierRepository tierRepository, UserTierRepository userTierRepository,
                        TierBenefitRepository tierBenefitRepository) {
                this.tierRepository = tierRepository;
                this.userTierRepository = userTierRepository;
                this.tierBenefitRepository = tierBenefitRepository;
        }

        public List<TierResponse> getAllTiers() {
                return tierRepository.findAll().stream()
                                .map(DtoMapper::toTierResponse)
                                .collect(Collectors.toList());
        }

        public TierResponse getTierById(UUID tierId) {
                MembershipTier tier = tierRepository.findById(tierId)
                                .orElseThrow(() -> new ResourceNotFoundException("Tier not found: " + tierId));
                return DtoMapper.toTierResponse(tier);
        }

        public UserTierResponse getUserTier(UUID userId) {

                UserTier userTier = userTierRepository
                                .findTopByUserIdOrderByAssignedAtDesc(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Tier not found for user"));

                return DtoMapper.toUserTierResponse(userTier);
        }

        @Transactional
        public TierResponse createTier(CreateTierRequest request) {

                if (tierRepository.existsByTierLevel(request.getTierLevel())) {
                        throw new IllegalArgumentException(
                                        "Tier already exists for level: " + request.getTierLevel());
                }

                MembershipTier tier = MembershipTier.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .tierLevel(request.getTierLevel())
                                .eligibilityRules(request.getEligibilityRules())
                                .build();

                List<TierBenefit> benefits = request.getTierBenefits().stream()
                                .map(b -> TierBenefit.builder()
                                                .tier(tier)
                                                .benefitType(b.getBenefitType())
                                                .value(b.getValue())
                                                .minOrderValue(b.getMinOrderValue())
                                                .maxDiscountCap(b.getMaxDiscountCap())
                                                .maxUsageLimit(b.getMaxUsageLimit())
                                                .description(b.getDescription())
                                                .build())
                                .toList();

                tier.setBenefits(benefits);

                tierRepository.save(tier);

                return DtoMapper.toTierResponse(tier);
        }

        @Transactional
        public void updateTier(UUID tierId, UpdateTierRequest request) {

                MembershipTier tier = tierRepository.findById(tierId)
                                .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));

                tier.updateFrom(request);

        }

        @Transactional
        public BenefitResponse createBenefit(UUID tierId, CreateTierBenefitRequest request) {
                MembershipTier tier = tierRepository.findById(tierId)
                                .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));

                if (tierBenefitRepository.existsByTierIdAndBenefitType(tierId, request.getBenefitType()))
                        throw new IllegalArgumentException(
                                        "Benefit already exists for type: " + request.getBenefitType());

                TierBenefit benefit = TierBenefit.builder()
                                .tier(tier)
                                .benefitType(request.getBenefitType())
                                .value(request.getValue())
                                .minOrderValue(request.getMinOrderValue())
                                .maxDiscountCap(request.getMaxDiscountCap())
                                .maxUsageLimit(request.getMaxUsageLimit())
                                .description(request.getDescription())
                                .build();

                tierBenefitRepository.save(benefit);
                return DtoMapper.toBenefitResponse(benefit);
        }

        @Transactional
        public void updateBenefit(UUID benefitId, UpdateTierBenefitRequest request) {

                TierBenefit benefit = tierBenefitRepository.findById(benefitId)
                                .orElseThrow(() -> new ResourceNotFoundException("Tier Benefit not found"));

                benefit.updateFrom(request);
        }

        @Transactional
        public void deleteBenefit(UUID benefitId) {

                TierBenefit benefit = tierBenefitRepository.findById(benefitId)
                                .orElseThrow(() -> new ResourceNotFoundException("Benefit not found"));

                tierBenefitRepository.delete(benefit);
        }

        @Transactional
        public void deleteTier(UUID tierId) {

                MembershipTier tier = tierRepository.findById(tierId)
                                .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));

                tierRepository.delete(tier);
        }
}
