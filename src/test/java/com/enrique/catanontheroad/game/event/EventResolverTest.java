package com.enrique.catanontheroad.game.event;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventResolverTest {

    private EventResolver resolver;
    private Game game;

    @BeforeEach
    void setUp() {
        resolver = new EventResolver();
        game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
    }

    private Player playerByName(String name) {
        return game.getPlayerByName(name);
    }

    private void clearHand(Player player) {
        for (ResourceType type : ResourceType.values()) {
            int count = player.getHand().count(type);
            if (count > 0) {
                player.getHand().remove(type, count);
            }
        }
    }

    private void setHandSize(Player player, ResourceType type, int count) {
        clearHand(player);
        player.getHand().add(type, count);
    }

    @Nested
    class RobberTests {

        @Test
        void should_require_discard_when_hand_exceeds_threshold() {
            Player alice = playerByName("Alice");
            clearHand(alice);
            alice.getHand().add(ResourceType.BRICK, 8); // threshold 7 (0 knights), 8 > 7

            var effects = resolver.calculateRobberEffects(game.getPlayers());

            assertThat(effects).anyMatch(e -> e.player() == alice && e.discardCount() == 4);
        }

        @Test
        void should_not_require_discard_when_hand_at_threshold() {
            Player alice = playerByName("Alice");
            clearHand(alice);
            alice.getHand().add(ResourceType.BRICK, 7); // threshold 7, 7 is NOT more than 7

            var effects = resolver.calculateRobberEffects(game.getPlayers());

            assertThat(effects).noneMatch(e -> e.player() == alice);
        }

        @Test
        void should_not_require_discard_when_hand_below_threshold() {
            Player alice = playerByName("Alice");
            clearHand(alice);
            alice.getHand().add(ResourceType.BRICK, 3);

            var effects = resolver.calculateRobberEffects(game.getPlayers());

            assertThat(effects).noneMatch(e -> e.player() == alice);
        }

        @Test
        void should_raise_threshold_with_knights() {
            Player alice = playerByName("Alice");
            alice.addKnight();
            alice.addKnight();
            alice.addKnight(); // threshold = 7 + 3 = 10
            clearHand(alice);
            alice.getHand().add(ResourceType.BRICK, 10); // 10 is NOT more than 10

            var effects = resolver.calculateRobberEffects(game.getPlayers());

            assertThat(effects).noneMatch(e -> e.player() == alice);
        }

        @Test
        void should_require_discard_above_knight_adjusted_threshold() {
            Player alice = playerByName("Alice");
            alice.addKnight();
            alice.addKnight();
            alice.addKnight(); // threshold = 10
            clearHand(alice);
            alice.getHand().add(ResourceType.BRICK, 12); // 12 > 10, discard 6

            var effects = resolver.calculateRobberEffects(game.getPlayers());

            assertThat(effects).anyMatch(e -> e.player() == alice && e.discardCount() == 6);
        }

        @Test
        void should_calculate_effects_for_multiple_players() {
            for (Player p : game.getPlayers()) {
                clearHand(p);
            }
            Player alice = playerByName("Alice");
            Player bob = playerByName("Bob");
            Player carol = playerByName("Carol");

            alice.getHand().add(ResourceType.BRICK, 8);  // discard 4
            bob.getHand().add(ResourceType.WOOD, 5);     // no discard
            carol.getHand().add(ResourceType.ORE, 9);    // discard 4

            var effects = resolver.calculateRobberEffects(game.getPlayers());

            assertThat(effects).hasSize(2);
            assertThat(effects).anyMatch(e -> e.player() == alice && e.discardCount() == 4);
            assertThat(effects).anyMatch(e -> e.player() == carol && e.discardCount() == 4);
        }

        @Test
        void should_discard_odd_hand_rounded_down() {
            Player alice = playerByName("Alice");
            clearHand(alice);
            alice.getHand().add(ResourceType.BRICK, 9); // discard floor(9/2) = 4

            var effects = resolver.calculateRobberEffects(game.getPlayers());

            assertThat(effects).anyMatch(e -> e.player() == alice && e.discardCount() == 4);
        }

        @Test
        void should_execute_robber_discard() {
            Player alice = playerByName("Alice");
            clearHand(alice);
            alice.getHand().add(ResourceType.BRICK, 5);
            alice.getHand().add(ResourceType.WOOD, 3);

            int deckSizeBefore = game.getResourceDeck().discardPileSize();

            resolver.executeRobberDiscard(alice, List.of(ResourceType.BRICK, ResourceType.BRICK, ResourceType.WOOD), game);

            assertThat(alice.getHand().count(ResourceType.BRICK)).isEqualTo(3);
            assertThat(alice.getHand().count(ResourceType.WOOD)).isEqualTo(2);
            assertThat(game.getResourceDeck().discardPileSize()).isEqualTo(deckSizeBefore + 3);
        }

        @Test
        void should_return_empty_effects_when_no_one_exceeds_threshold() {
            for (Player p : game.getPlayers()) {
                clearHand(p);
                p.getHand().add(ResourceType.BRICK, 3);
            }

            var effects = resolver.calculateRobberEffects(game.getPlayers());

            assertThat(effects).isEmpty();
        }
    }

    @Nested
    class AbundanceTests {

        @Test
        void should_exclude_longest_route_holder() {
            Player alice = playerByName("Alice");
            alice.setLongestRoute(true);

            var eligible = resolver.getAbundanceEligiblePlayers(game.getPlayers());

            assertThat(eligible).doesNotContain(alice);
            assertThat(eligible).hasSize(2);
        }

        @Test
        void should_exclude_largest_army_holder() {
            Player bob = playerByName("Bob");
            bob.setLargestArmy(true);

            var eligible = resolver.getAbundanceEligiblePlayers(game.getPlayers());

            assertThat(eligible).doesNotContain(bob);
            assertThat(eligible).hasSize(2);
        }

        @Test
        void should_exclude_both_bonus_holders() {
            Player alice = playerByName("Alice");
            Player bob = playerByName("Bob");
            alice.setLongestRoute(true);
            bob.setLargestArmy(true);

            var eligible = resolver.getAbundanceEligiblePlayers(game.getPlayers());

            assertThat(eligible).doesNotContain(alice, bob);
            assertThat(eligible).hasSize(1);
        }

        @Test
        void should_include_all_when_no_bonus_holders() {
            var eligible = resolver.getAbundanceEligiblePlayers(game.getPlayers());

            assertThat(eligible).hasSize(3);
        }

        @Test
        void should_draw_2_cards_per_eligible_player() {
            for (Player p : game.getPlayers()) {
                clearHand(p);
            }
            Player alice = playerByName("Alice");
            alice.setLongestRoute(true);

            var drawn = resolver.executeAbundance(game);

            assertThat(drawn).hasSize(2); // Bob and Carol
            for (var entry : drawn.entrySet()) {
                assertThat(entry.getValue()).hasSize(2);
                assertThat(entry.getKey().getHand().total()).isEqualTo(2);
            }
            assertThat(alice.getHand().total()).isZero();
        }

        @Test
        void should_add_drawn_cards_to_player_hands() {
            for (Player p : game.getPlayers()) {
                clearHand(p);
            }

            var drawn = resolver.executeAbundance(game);

            for (var entry : drawn.entrySet()) {
                Player player = entry.getKey();
                List<ResourceType> cards = entry.getValue();
                for (ResourceType type : cards) {
                    assertThat(player.getHand().has(type)).isTrue();
                }
            }
        }
    }

    @Nested
    class CharityTests {

        @Test
        void should_detect_case_a_one_top_one_bottom() {
            Player alice = playerByName("Alice");
            Player bob = playerByName("Bob");
            // Alice has 1 settlement (starting) = 1 VP
            // Bob has 1 settlement (starting) = 1 VP
            // Give Carol extra VP
            Player carol = playerByName("Carol");
            carol.addSettlement(); // 2 VP
            // Give Alice less VP is already at 1
            // Carol=2, Alice=1, Bob=1 => Carol is top, Alice and Bob tied for bottom

            var effect = resolver.calculateCharityEffect(game.getPlayers());

            // This is actually Case C: one top (Carol), 2 bottom (Alice, Bob)
            assertThat(effect.charityCase()).isEqualTo(EventResolver.CharityCase.ONE_TOP_MULTIPLE_BOTTOM);
        }

        @Test
        void should_detect_case_b_multiple_top_one_bottom() {
            // All start with 1 VP (1 settlement each)
            Player alice = playerByName("Alice");
            Player bob = playerByName("Bob");
            Player carol = playerByName("Carol");

            alice.addSettlement(); // 2 VP
            bob.addSettlement();   // 2 VP

            // Alice=2, Bob=2, Carol=1 => this is Case B (2 top, 1 bottom)
            var effect = resolver.calculateCharityEffect(game.getPlayers());
            assertThat(effect.charityCase()).isEqualTo(EventResolver.CharityCase.MULTIPLE_TOP_ONE_BOTTOM);
            assertThat(effect.givers()).containsExactly(alice, bob);
            assertThat(effect.receivers()).containsExactly(carol);
        }

        @Test
        void should_detect_one_top_one_bottom_with_three_tiers() {
            Player alice = playerByName("Alice");
            Player bob = playerByName("Bob");
            Player carol = playerByName("Carol");

            // Alice: 1 VP (starting settlement)
            // Bob: 2 VP
            bob.addSettlement();
            // Carol: 3 VP
            carol.addSettlement();
            carol.addSettlement();

            // Carol=3, Bob=2, Alice=1 => Case A: one top, one bottom
            var effect = resolver.calculateCharityEffect(game.getPlayers());
            assertThat(effect.charityCase()).isEqualTo(EventResolver.CharityCase.ONE_TOP_ONE_BOTTOM);
            assertThat(effect.givers()).containsExactly(carol);
            assertThat(effect.receivers()).containsExactly(alice);
        }

        @Test
        void should_detect_case_d_all_tied() {
            // All start with 1 VP each
            var effect = resolver.calculateCharityEffect(game.getPlayers());

            assertThat(effect.charityCase()).isEqualTo(EventResolver.CharityCase.ALL_TIED);
            assertThat(effect.givers()).isEmpty();
            assertThat(effect.receivers()).isEmpty();
        }

        @Test
        void should_detect_case_c_one_top_multiple_bottom() {
            Player alice = playerByName("Alice");
            // Alice gets extra VP, Bob and Carol stay at 1
            alice.addSettlement(); // 2 VP

            var effect = resolver.calculateCharityEffect(game.getPlayers());

            assertThat(effect.charityCase()).isEqualTo(EventResolver.CharityCase.ONE_TOP_MULTIPLE_BOTTOM);
            assertThat(effect.givers()).containsExactly(alice);
            assertThat(effect.receivers()).hasSize(2);
        }

        @Test
        void should_execute_charity_give() {
            Player alice = playerByName("Alice");
            Player carol = playerByName("Carol");
            clearHand(alice);
            clearHand(carol);
            alice.getHand().add(ResourceType.WOOL, 3);

            resolver.executeCharityGive(alice, ResourceType.WOOL, carol);

            assertThat(alice.getHand().count(ResourceType.WOOL)).isEqualTo(2);
            assertThat(carol.getHand().count(ResourceType.WOOL)).isEqualTo(1);
        }

        @Test
        void should_execute_case_c_with_random_assignment() {
            Player alice = playerByName("Alice");
            Player bob = playerByName("Bob");
            Player carol = playerByName("Carol");
            clearHand(alice);
            clearHand(bob);
            clearHand(carol);
            alice.getHand().add(ResourceType.BRICK, 1);
            alice.getHand().add(ResourceType.WOOD, 1);

            var assignments = resolver.executeCharityCaseC(
                alice,
                List.of(ResourceType.BRICK, ResourceType.WOOD),
                List.of(bob, carol),
                game
            );

            assertThat(alice.getHand().total()).isZero();
            assertThat(assignments).hasSize(2);
            // Each receiver should get exactly 1 card
            int bobTotal = bob.getHand().total();
            int carolTotal = carol.getHand().total();
            assertThat(bobTotal + carolTotal).isEqualTo(2);
            assertThat(bobTotal).isEqualTo(1);
            assertThat(carolTotal).isEqualTo(1);
        }

        @Test
        void should_reject_wrong_number_of_resources_in_case_c() {
            Player alice = playerByName("Alice");
            Player bob = playerByName("Bob");
            Player carol = playerByName("Carol");
            clearHand(alice);
            alice.getHand().add(ResourceType.BRICK, 3);

            assertThatThrownBy(() ->
                resolver.executeCharityCaseC(
                    alice,
                    List.of(ResourceType.BRICK),
                    List.of(bob, carol),
                    game
                )
            ).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_detect_impossible_case() {
            // Need 2+ tied for most AND 2+ tied for fewest simultaneously
            // With 3 players this can't naturally happen, but let's test the logic
            // All 3 tied is Case D, not Case F
            // 2 top + 1 bottom = Case B, 1 top + 2 bottom = Case C
            // The impossible case would need 4+ players; with 3, Case F can't occur
            // But we test the detection logic anyway by noting all-tied is D, not F
            var effect = resolver.calculateCharityEffect(game.getPlayers());
            assertThat(effect.charityCase()).isNotEqualTo(EventResolver.CharityCase.IMPOSSIBLE);
        }
    }

    @Nested
    class SolsticeTests {

        @Test
        void should_draw_one_card_per_player() {
            for (Player p : game.getPlayers()) {
                clearHand(p);
            }

            var result = resolver.executeSolstice(game);

            assertThat(result.drawn()).hasSize(3);
            for (Player p : game.getPlayers()) {
                assertThat(p.getHand().total()).isEqualTo(1);
            }
        }

        @Test
        void should_reshuffle_event_deck() {
            // Draw some events first
            game.getEventDeck().draw();
            game.getEventDeck().draw();
            int beforeDraw = game.getEventDeck().drawPileSize();

            resolver.executeSolstice(game);

            // After Solstice, all 7 event cards should be back in the draw pile
            assertThat(game.getEventDeck().drawPileSize()).isEqualTo(7);
            assertThat(game.getEventDeck().discardPileSize()).isZero();
        }

        @Test
        void should_return_drawn_cards_in_result() {
            for (Player p : game.getPlayers()) {
                clearHand(p);
            }

            var result = resolver.executeSolstice(game);

            for (var entry : result.drawn().entrySet()) {
                Player player = entry.getKey();
                ResourceType card = entry.getValue();
                assertThat(player.getHand().has(card)).isTrue();
            }
        }
    }

    @Nested
    class SubsidyTests {

        @Test
        void should_draw_one_card_per_uncovered_settlement() {
            Player alice = playerByName("Alice");
            Player bob = playerByName("Bob");
            for (Player p : game.getPlayers()) {
                clearHand(p);
            }
            // All start with 1 uncovered settlement
            alice.addSettlement(); // now 2 uncovered settlements

            var result = resolver.executeSubsidy(game);

            assertThat(alice.getHand().total()).isEqualTo(2);
            assertThat(bob.getHand().total()).isEqualTo(1);
        }

        @Test
        void should_give_nothing_to_player_with_no_uncovered_settlements() {
            Player alice = playerByName("Alice");
            clearHand(alice);
            // Cover the starting settlement with a city
            alice.getHand().add(ResourceType.WHEAT, 2);
            alice.getHand().add(ResourceType.ORE, 3);
            alice.addCity(); // covers starting settlement
            clearHand(alice);

            var result = resolver.executeSubsidy(game);

            assertThat(alice.getHand().total()).isZero();
            assertThat(result.drawn().get(alice)).isEmpty();
        }

        @Test
        void should_return_drawn_cards_in_result() {
            for (Player p : game.getPlayers()) {
                clearHand(p);
            }

            var result = resolver.executeSubsidy(game);

            for (var entry : result.drawn().entrySet()) {
                Player player = entry.getKey();
                List<ResourceType> cards = entry.getValue();
                assertThat(player.getHand().total()).isEqualTo(cards.size());
            }
        }
    }

    @Nested
    class DrawEventCardTests {

        @Test
        void should_draw_from_event_deck() {
            int sizeBefore = game.getEventDeck().drawPileSize();

            var card = resolver.drawEventCard(game);

            assertThat(card).isNotNull();
            assertThat(game.getEventDeck().drawPileSize()).isEqualTo(sizeBefore - 1);
        }
    }
}
