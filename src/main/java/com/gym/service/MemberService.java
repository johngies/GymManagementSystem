package com.gym.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.dto.MemberRequest;
import com.gym.dto.MemberResponse;
import com.gym.entity.Member;
import com.gym.exception.DuplicateResourceException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::fromEntity)
                .toList();
    }

    public MemberResponse getMemberById(Long id) {
        return memberRepository.findById(id)
                .map(MemberResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    public MemberResponse getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .map(MemberResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with email: " + email));
    }

    public Member getMemberEntity(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + id));
    }

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Member with email " + request.email() + " already exists");
        }

        Member member = Member.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .dateOfBirth(request.dateOfBirth())
                .build();

        Member savedMember = memberRepository.save(member);
        return MemberResponse.fromEntity(savedMember);
    }

    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = getMemberEntity(id);

        if (!member.getEmail().equalsIgnoreCase(request.email()) && memberRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Member with email " + request.email() + " already exists");
        }

        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setEmail(request.email());
        member.setPhone(request.phone());
        member.setDateOfBirth(request.dateOfBirth());

        Member updatedMember = memberRepository.save(member);
        return MemberResponse.fromEntity(updatedMember);
    }

    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
}