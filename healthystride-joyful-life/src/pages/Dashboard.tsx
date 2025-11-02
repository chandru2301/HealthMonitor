import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Footprints, Flame, Clock, TrendingUp, Plus } from "lucide-react";
import { Layout } from "@/components/Layout";
import { StatCard } from "@/components/StatCard";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { api, HealthMetrics, Activity, DashboardSummary } from "@/lib/api";
import { useUser } from "@/contexts/UserContext";
import { toast } from "sonner";
import { format } from "date-fns";

const Dashboard = () => {
  const { currentUserId } = useUser();
  const navigate = useNavigate();
  const [todayMetrics, setTodayMetrics] = useState<HealthMetrics | null>(null);
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [recentActivities, setRecentActivities] = useState<Activity[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!currentUserId) {
      navigate("/users");
      return;
    }
    loadDashboardData();
  }, [currentUserId, navigate]);

  const loadDashboardData = async () => {
    if (!currentUserId) return;
    
    try {
      const [metrics, summaryData, activities] = await Promise.all([
        api.metrics.getToday(currentUserId),
        api.users.getDashboardSummary(currentUserId),
        api.activities.getAll(currentUserId)
      ]);
      
      setTodayMetrics(metrics);
      setSummary(summaryData);
      setRecentActivities(activities.slice(0, 5));
    } catch (error) {
      // Error toast is handled by API service
      console.error("Failed to load dashboard data:", error);
    } finally {
      setLoading(false);
    }
  };

  if (!currentUserId) return null;

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold">Dashboard</h1>
            <p className="text-muted-foreground">
              {format(new Date(), "EEEE, MMMM d, yyyy")}
            </p>
          </div>
          <div className="flex gap-2">
            <Button onClick={() => navigate("/metrics")} variant="outline" size="sm">
              <Plus className="h-4 w-4 mr-2" />
              Add Metrics
            </Button>
            <Button onClick={() => navigate("/activities")} size="sm">
              <Plus className="h-4 w-4 mr-2" />
              Log Activity
            </Button>
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <StatCard
            title="Steps Today"
            value={todayMetrics?.steps?.toLocaleString() || "0"}
            icon={Footprints}
            subtitle="Keep moving!"
            variant="primary"
          />
          <StatCard
            title="Calories Burned"
            value={todayMetrics?.caloriesBurned?.toFixed(0) || "0"}
            icon={Flame}
            subtitle="kcal"
            variant="accent"
          />
          <StatCard
            title="Active Minutes"
            value={todayMetrics?.activeMinutes || "0"}
            icon={Clock}
            subtitle="minutes"
            variant="success"
          />
          <StatCard
            title="Net Calories"
            value={todayMetrics?.netCalories?.toFixed(0) || "0"}
            icon={TrendingUp}
            subtitle="consumed - burned"
          />
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Today's Summary</CardTitle>
              <CardDescription>Your health metrics for today</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {todayMetrics ? (
                <>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <p className="text-sm text-muted-foreground">Distance</p>
                      <p className="text-2xl font-bold">{todayMetrics.distanceKm?.toFixed(2) || "0"} km</p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Water Intake</p>
                      <p className="text-2xl font-bold">{todayMetrics.waterIntakeLiters?.toFixed(1) || "0"} L</p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Sleep</p>
                      <p className="text-2xl font-bold">{todayMetrics.sleepHours?.toFixed(1) || "0"} hrs</p>
                    </div>
                    <div>
                      <p className="text-sm text-muted-foreground">Heart Rate</p>
                      <p className="text-2xl font-bold">{todayMetrics.heartRateAvg || "0"} bpm</p>
                    </div>
                  </div>
                  {summary && (
                    <div className="pt-4 border-t space-y-2">
                      <p className="text-sm text-muted-foreground">Daily Targets</p>
                      <div className="grid grid-cols-2 gap-2 text-sm">
                        <div>BMR: <span className="font-semibold">{summary.bmr.toFixed(0)} kcal</span></div>
                        <div>TDEE: <span className="font-semibold">{summary.tdee.toFixed(0)} kcal</span></div>
                      </div>
                    </div>
                  )}
                </>
              ) : (
                <p className="text-muted-foreground">No metrics logged for today</p>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Recent Activities</CardTitle>
              <CardDescription>Your latest workouts and exercises</CardDescription>
            </CardHeader>
            <CardContent>
              {recentActivities.length > 0 ? (
                <div className="space-y-3">
                  {recentActivities.map((activity) => (
                    <div key={activity.id} className="flex items-center justify-between p-3 rounded-lg bg-muted/50">
                      <div>
                        <p className="font-medium">{activity.activityType}</p>
                        <p className="text-sm text-muted-foreground">
                          {format(new Date(activity.startTime), "MMM d, h:mm a")}
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="font-semibold">{activity.durationMinutes?.toFixed(0)} min</p>
                        <p className="text-sm text-muted-foreground">{activity.caloriesBurned?.toFixed(0)} kcal</p>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-muted-foreground">No activities logged yet</p>
              )}
            </CardContent>
          </Card>
        </div>
      </div>
    </Layout>
  );
};

export default Dashboard;
