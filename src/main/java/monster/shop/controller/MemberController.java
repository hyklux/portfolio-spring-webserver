package monster.shop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import monster.shop.domain.dto.MemberFormDto;
import monster.shop.domain.model.Member;
import monster.shop.domain.service.MemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입폼 정보 요청.
     * @param model 클라이언트에 전달할 정보.
     * @return View 페이지 정보 호출.
     */
    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    /**
     * 회원 가입폼 - 회원가입 요청
     * @param dto 클라이언트 form 입력된 정보.
     * @param bindingResult validate 검사시 오류에 대한 정보
     * @param model 클라이언트에 전달할 정보
     * @return main 화면으로 redirect 처리.
     */
    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(dto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            // 중복 가입 방지코드
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }

    /**
     * 로그인 가입폼 요청
     * @return 페이지 정보
     */
    @GetMapping(value = "/login")
    public String loginMember(){
        return "/member/memberLoginForm";
    }

    /**
     * 로그인 에러 처리
     * @param model 클라이언트에 전달한 내용 정보 추가.
     * @return 페이지 정보
     */
    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }
}
