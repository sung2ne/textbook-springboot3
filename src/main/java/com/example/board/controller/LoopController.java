// 새 파일: src/main/java/com/example/board/controller/LoopController.java
package com.example.board.controller;

import com.example.board.dto.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/loop")
public class LoopController {

    // GET /loop/demo - 반복문 데모 페이지
    @GetMapping("/demo")
    public String demo(Model model) {
        // 1. 리스트 데이터
        List<String> fruits = List.of("사과", "바나나", "오렌지", "포도", "딸기");
        model.addAttribute("fruits", fruits);

        // 2. 숫자 범위 (1~5)
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        model.addAttribute("numbers", numbers);

        // 3. 객체 리스트 (상품 목록)
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, "노트북", 1500000, "전자기기", true));
        products.add(new Product(2L, "마우스", 30000, "전자기기", true));
        products.add(new Product(3L, "키보드", 80000, "전자기기", false));
        products.add(new Product(4L, "모니터", 350000, "전자기기", true));
        products.add(new Product(5L, "스피커", 120000, "전자기기", false));
        model.addAttribute("products", products);

        // 4. Map 데이터
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("이름", "홍길동");
        userInfo.put("이메일", "hong@example.com");
        userInfo.put("전화번호", "010-1234-5678");
        userInfo.put("주소", "서울시 강남구");
        model.addAttribute("userInfo", userInfo);

        return "loop/demo";
    }
}
