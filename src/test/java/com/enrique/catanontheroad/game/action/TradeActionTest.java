package com.enrique.catanontheroad.game.action;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TradeActionTest {

    private Game game;
    private Player alice;
    private Player bob;
    private TradeAction tradeAction;

    @BeforeEach
    void setUp() {
        game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        alice = game.getPlayers().get(0);
        bob = game.getPlayers().get(1);
        tradeAction = new TradeAction();

        // Clear and set up specific hands for testing
        clearHand(alice);
        clearHand(bob);
    }

    private void clearHand(Player player) {
        for (ResourceType type : ResourceType.values()) {
            while (player.getHand().has(type)) {
                player.getHand().remove(type);
            }
        }
    }

    @Test
    void validate_offer_should_pass_with_valid_offer() {
        alice.getHand().add(ResourceType.BRICK);

        var result = tradeAction.validateOffer(alice, List.of(ResourceType.BRICK), ResourceType.WOOL);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_offer_should_fail_with_empty_offered_types() {
        var result = tradeAction.validateOffer(alice, List.of(), ResourceType.WOOL);

        assertThat(result.isValid()).isFalse();
        assertThat(result.message()).contains("offer");
    }

    @Test
    void validate_offer_should_fail_when_offerer_lacks_resource() {
        var result = tradeAction.validateOffer(alice, List.of(ResourceType.BRICK), ResourceType.WOOL);

        assertThat(result.isValid()).isFalse();
        assertThat(result.message()).contains("brick");
    }

    @Test
    void validate_offer_should_fail_when_requested_type_in_offered() {
        alice.getHand().add(ResourceType.BRICK);

        var result = tradeAction.validateOffer(alice, List.of(ResourceType.BRICK), ResourceType.BRICK);

        assertThat(result.isValid()).isFalse();
        assertThat(result.message()).contains("cannot");
    }

    @Test
    void can_auto_decline_should_return_true_when_target_lacks_requested() {
        assertThat(tradeAction.canAutoDecline(bob, ResourceType.WOOL)).isTrue();
    }

    @Test
    void can_auto_decline_should_return_false_when_target_has_requested() {
        bob.getHand().add(ResourceType.WOOL);

        assertThat(tradeAction.canAutoDecline(bob, ResourceType.WOOL)).isFalse();
    }

    @Test
    void execute_trade_should_swap_resources() {
        alice.getHand().add(ResourceType.BRICK);
        bob.getHand().add(ResourceType.WOOL);

        var offer = new TradeAction.TradeOffer(alice, List.of(ResourceType.BRICK), ResourceType.WOOL, bob);

        var result = tradeAction.executeTrade(offer, ResourceType.BRICK, game);

        assertThat(result.success()).isTrue();
        assertThat(alice.getHand().has(ResourceType.BRICK)).isFalse();
        assertThat(alice.getHand().has(ResourceType.WOOL)).isTrue();
        assertThat(bob.getHand().has(ResourceType.WOOL)).isFalse();
        assertThat(bob.getHand().has(ResourceType.BRICK)).isTrue();
    }

    @Test
    void execute_trade_should_give_target_bonus_card() {
        alice.getHand().add(ResourceType.BRICK);
        bob.getHand().add(ResourceType.WOOL);
        int bobHandBefore = bob.getHand().total();

        var offer = new TradeAction.TradeOffer(alice, List.of(ResourceType.BRICK), ResourceType.WOOL, bob);
        tradeAction.executeTrade(offer, ResourceType.BRICK, game);

        // Bob gave 1, received 1, and got 1 bonus = net +1
        assertThat(bob.getHand().total()).isEqualTo(bobHandBefore + 1);
    }

    @Test
    void execute_trade_should_fail_with_invalid_choice() {
        alice.getHand().add(ResourceType.BRICK);
        bob.getHand().add(ResourceType.WOOL);

        var offer = new TradeAction.TradeOffer(alice, List.of(ResourceType.BRICK), ResourceType.WOOL, bob);

        // Try to choose WOOD which wasn't offered
        var result = tradeAction.executeTrade(offer, ResourceType.WOOD, game);

        assertThat(result.success()).isFalse();
    }

    @Test
    void validate_offer_should_fail_with_null_offered_types() {
        var result = tradeAction.validateOffer(alice, null, ResourceType.WOOL);

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void trade_result_declined_should_have_correct_fields() {
        var result = TradeAction.TradeResult.declined("Bob said no");

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Bob said no");
        assertThat(result.givenType()).isNull();
        assertThat(result.receivedType()).isNull();
    }

    @Test
    void trade_result_auto_declined_should_have_correct_fields() {
        var result = TradeAction.TradeResult.autoDeclined("No wool");

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("No wool");
    }

    @Test
    void trade_result_accepted_should_have_correct_fields() {
        var result = TradeAction.TradeResult.accepted(ResourceType.BRICK, ResourceType.WOOL);

        assertThat(result.success()).isTrue();
        assertThat(result.givenType()).isEqualTo(ResourceType.BRICK);
        assertThat(result.receivedType()).isEqualTo(ResourceType.WOOL);
    }

    @Test
    void validate_offer_with_multiple_types() {
        alice.getHand().add(ResourceType.BRICK);
        alice.getHand().add(ResourceType.WOOD);

        var result = tradeAction.validateOffer(
            alice,
            List.of(ResourceType.BRICK, ResourceType.WOOD),
            ResourceType.WOOL
        );

        assertThat(result.isValid()).isTrue();
    }
}
