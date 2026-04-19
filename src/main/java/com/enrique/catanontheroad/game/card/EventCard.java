package com.enrique.catanontheroad.game.card;

public sealed interface EventCard permits EventCard.Robber, EventCard.Abundance, EventCard.Charity, EventCard.Solstice, EventCard.Subsidy {

    String name();

    record Robber() implements EventCard {
        @Override
        public String name() {
            return "Robber";
        }
    }

    record Abundance() implements EventCard {
        @Override
        public String name() {
            return "Abundance";
        }
    }

    record Charity() implements EventCard {
        @Override
        public String name() {
            return "Charity";
        }
    }

    record Solstice() implements EventCard {
        @Override
        public String name() {
            return "Solstice";
        }
    }

    record Subsidy() implements EventCard {
        @Override
        public String name() {
            return "Subsidy";
        }
    }
}
