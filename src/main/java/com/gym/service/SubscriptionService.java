package com.gym.service;

import com.gym.dto.SubscriptionRequest;
import com.gym.dto.SubscriptionResponse;
import com.gym.entity.Member;
import com.gym.entity.Subscription;
import com.gym.exception.DuplicateResourceException;
import com.gym.exception.InvalidOperationException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MemberService memberService;

    public List<SubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAll()
                .stream()
                .map(SubscriptionResponse::fromEntity)
                .toList();
    }

    public SubscriptionResponse getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .map(SubscriptionResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
    }

    public SubscriptionResponse getSubscriptionByMemberId(Long memberId) {
        return subscriptionRepository.findByMemberId(memberId)
                .map(SubscriptionResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found for member id: " + memberId));
    }

    @Transactional
    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new InvalidOperationException("Subscription end date cannot be before start date");
        }

        if (subscriptionRepository.existsByMemberId(request.memberId())) {
            throw new DuplicateResourceException("Member with id " + request.memberId() + " already has a subscription");
        }

        Member member = memberService.getMemberEntity(request.memberId());

        Subscription subscription = Subscription.builder()
                .member(member)
                .planName(request.planName())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .active(true)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        return SubscriptionResponse.fromEntity(savedSubscription);
    }

    @Transactional
    public SubscriptionResponse updateSubscription(Long id, SubscriptionRequest request) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));

        if (request.endDate().isBefore(request.startDate())) {
            throw new InvalidOperationException("Subscription end date cannot be before start date");
        }

        if (!subscription.getMember().getId().equals(request.memberId())) {
            if (subscriptionRepository.existsByMemberId(request.memberId())) {
                throw new DuplicateResourceException("Member with id " + request.memberId() + " already has a subscription");
            }
            Member newMember = memberService.getMemberEntity(request.memberId());
            subscription.setMember(newMember);
        }

        subscription.setPlanName(request.planName());
        subscription.setStartDate(request.startDate());
        subscription.setEndDate(request.endDate());

        Subscription updatedSubscription = subscriptionRepository.save(subscription);
        return SubscriptionResponse.fromEntity(updatedSubscription);
    }

    @Transactional
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + id);
        }
        subscriptionRepository.deleteById(id);
    }
}