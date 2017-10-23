package ka20er.aurinwayfinder.Direction;

import java.util.ArrayList;
import java.util.List;

public class Direction {
    private List<Route> routes = new ArrayList<>();

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}