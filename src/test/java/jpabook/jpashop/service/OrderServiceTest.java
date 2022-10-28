package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
@RunWith(SpringRunner.class)
public class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() {
        // Given
        Member member = createMember();

        Book book = createBook("시골JPA", 10000, 10);

        int orderCount = 2;
        // When
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        // Then
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        Assert.assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8 ,book.getStockQuantity());
    }



    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() {
        // Given
        Member member = createMember();
        Item item = createBook("시골JPA", 10000, 10);

        int ordreCount = 11;
        // When
        orderService.order(member.getId(), item.getId(), ordreCount);
        // Then
        Assert.fail("재고 수량 부족 예외가 발생한다.");
    }

    @Test
    public void 주문취소() {
        // Given
        Member member = createMember();
        Item item = createBook("시골JPA", 10000, 10);

        int ordreCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), ordreCount);
        // When
        orderService.cancelOrder(orderId);
        // Then
        Order order = orderRepository.findOne(orderId);

        Assert.assertEquals("주문 취소 상태",OrderStatus.CANCEL,order.getStatus());
        Assert.assertEquals("재고 원복",10,item.getStockQuantity());
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("member1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);
        return member;
    }

}