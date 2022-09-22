package com.d202.assemble.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.d202.assemble.dto.User;
import com.d202.assemble.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(value="User")
@RestController
@RequestMapping("/user")
//lombok 생성자 주입 => 초기화되지않은 final 변수에 주입시킴 (생성자가 1개일때 가능)
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@ApiOperation(value="회원정보 상세 조회")
	@GetMapping("/{seq}")
	public ResponseEntity<?> getUserDetail(@PathVariable int seq){
		Optional<User> userOp = userService.findUserBySeq(seq);
		if(userOp.isPresent()) {
			return new ResponseEntity<User>(userOp.get(), HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value="네이버 로그인")
	@PostMapping("/naver/login")
	public ResponseEntity<?> naverLogin(@RequestBody String token){
		Map<String, Object> userInfo = userService.getNaverUserInfo(token);
		if(userInfo!=null) {
			String email = userInfo.get("email").toString();
			//가입된 유저인지 확인
			Optional<User> userOp = userService.findUserByEmail(email);
			if(userOp.isPresent()) {
				return new ResponseEntity<User>(userOp.get(), HttpStatus.OK);
			}
			else {//가입 = DB save
				User user = new User();
				user.setEmail(userInfo.get("email").toString());
				if(userService.insertUser(user)) {
					return new ResponseEntity<User>(user, HttpStatus.OK);
				}
			}	
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value="카카오 로그인")
	@PostMapping("/kakao/login")
	public ResponseEntity<?> kakaoLogin(@RequestBody String token){
		Map<String, Object> userInfo = userService.getKakaoUserInfo(token);
		if(userInfo!=null) {
			String email = userInfo.get("email").toString();
			//가입된 유저인지 확인
			Optional<User> userOp = userService.findUserByEmail(email);
			if(userOp.isPresent()) {
				return new ResponseEntity<User>(userOp.get(), HttpStatus.OK);
			}
			else {//가입 = DB save
				User user = new User();
				user.setEmail(userInfo.get("email").toString());
				if(userService.insertUser(user)) {
					return new ResponseEntity<User>(user, HttpStatus.OK);
				}
			}	
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}
	
	@ApiOperation(value="회원탈퇴")
	@DeleteMapping("/{seq}")
	public ResponseEntity<?> disJoin(@PathVariable int seq){
		try {
			userService.deleteUser(seq);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
}
