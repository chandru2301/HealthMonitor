import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Layout } from "@/components/Layout";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { StatCard } from "@/components/StatCard";
import { Footprints, Flame, TrendingUp, Clock } from "lucide-react";
import { api, WeeklyStats } from "@/lib/api";
import { useUser } from "@/contexts/UserContext";
import { format, subDays } from "date-fns";
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";

const Statistics = () => {
  const { currentUserId } = useUser();
  const navigate = useNavigate();
  const [weeklyStats, setWeeklyStats] = useState<WeeklyStats | null>(null);
  const [chartData, setChartData] = useState<any[]>([]);

  useEffect(() => {
    if (!currentUserId) {
      navigate("/users");
      return;
    }
    loadStatistics();
  }, [currentUserId, navigate]);

  const loadStatistics = async () => {
    if (!currentUserId) return;
    
    try {
      const startDate = format(subDays(new Date(), 6), "yyyy-MM-dd");
      const endDate = format(new Date(), "yyyy-MM-dd");
      
      const [stats, metrics] = await Promise.all([
        api.dashboard.getWeeklyStats(currentUserId, startDate),
        api.metrics.getRange(currentUserId, startDate, endDate)
      ]);
      
      setWeeklyStats(stats);
      
      // Format data for charts
      const formattedData = metrics.map((metric: any) => ({
        date: format(new Date(metric.date), "MMM d"),
        steps: metric.steps || 0,
        caloriesConsumed: metric.caloriesConsumed || 0,
        caloriesBurned: metric.caloriesBurned || 0,
        activeMinutes: metric.activeMinutes || 0
      }));
      
      setChartData(formattedData);
    } catch (error) {
      // Error toast is handled by API service
      console.error("Failed to load statistics:", error);
    }
  };

  if (!weeklyStats) return null;

  return (
    <Layout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold">Statistics</h1>
          <p className="text-muted-foreground">
            Week of {format(new Date(weeklyStats.startDate), "MMM d")} - {format(new Date(weeklyStats.endDate), "MMM d, yyyy")}
          </p>
        </div>

        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          <StatCard
            title="Total Steps"
            value={weeklyStats.totalSteps.toLocaleString()}
            icon={Footprints}
            subtitle={`Avg: ${weeklyStats.averageStepsPerDay.toFixed(0)}/day`}
            variant="primary"
          />
          <StatCard
            title="Calories Burned"
            value={weeklyStats.totalCaloriesBurned.toFixed(0)}
            icon={Flame}
            subtitle="kcal this week"
            variant="accent"
          />
          <StatCard
            title="Active Minutes"
            value={weeklyStats.totalActiveMinutes}
            icon={Clock}
            subtitle={`Avg: ${weeklyStats.averageActiveMinutesPerDay.toFixed(0)}/day`}
            variant="success"
          />
          <StatCard
            title="Distance"
            value={`${weeklyStats.totalDistanceKm.toFixed(1)} km`}
            icon={TrendingUp}
            subtitle="total distance"
          />
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Steps Over Time</CardTitle>
              <CardDescription>Daily step count for the past week</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                  <XAxis dataKey="date" className="text-xs" />
                  <YAxis className="text-xs" />
                  <Tooltip 
                    contentStyle={{ 
                      backgroundColor: "hsl(var(--card))", 
                      border: "1px solid hsl(var(--border))",
                      borderRadius: "var(--radius)"
                    }} 
                  />
                  <Legend />
                  <Line 
                    type="monotone" 
                    dataKey="steps" 
                    stroke="hsl(var(--primary))" 
                    strokeWidth={2}
                    name="Steps"
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Calories Tracking</CardTitle>
              <CardDescription>Consumed vs burned calories</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                  <XAxis dataKey="date" className="text-xs" />
                  <YAxis className="text-xs" />
                  <Tooltip 
                    contentStyle={{ 
                      backgroundColor: "hsl(var(--card))", 
                      border: "1px solid hsl(var(--border))",
                      borderRadius: "var(--radius)"
                    }} 
                  />
                  <Legend />
                  <Line 
                    type="monotone" 
                    dataKey="caloriesConsumed" 
                    stroke="hsl(var(--accent))" 
                    strokeWidth={2}
                    name="Consumed"
                  />
                  <Line 
                    type="monotone" 
                    dataKey="caloriesBurned" 
                    stroke="hsl(var(--success))" 
                    strokeWidth={2}
                    name="Burned"
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>

          <Card className="md:col-span-2">
            <CardHeader>
              <CardTitle>Active Minutes Per Day</CardTitle>
              <CardDescription>Daily activity duration</CardDescription>
            </CardHeader>
            <CardContent>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                  <XAxis dataKey="date" className="text-xs" />
                  <YAxis className="text-xs" />
                  <Tooltip 
                    contentStyle={{ 
                      backgroundColor: "hsl(var(--card))", 
                      border: "1px solid hsl(var(--border))",
                      borderRadius: "var(--radius)"
                    }} 
                  />
                  <Legend />
                  <Bar 
                    dataKey="activeMinutes" 
                    fill="hsl(var(--success))" 
                    name="Active Minutes"
                    radius={[8, 8, 0, 0]}
                  />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Weekly Summary</CardTitle>
            <CardDescription>Detailed breakdown of your week</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid gap-4 md:grid-cols-3">
              <div className="p-4 rounded-lg bg-muted/50">
                <p className="text-sm text-muted-foreground mb-1">Total Calories Consumed</p>
                <p className="text-2xl font-bold">{weeklyStats.totalCaloriesConsumed.toFixed(0)} kcal</p>
              </div>
              <div className="p-4 rounded-lg bg-muted/50">
                <p className="text-sm text-muted-foreground mb-1">Total Calories Burned</p>
                <p className="text-2xl font-bold">{weeklyStats.totalCaloriesBurned.toFixed(0)} kcal</p>
              </div>
              <div className="p-4 rounded-lg bg-muted/50">
                <p className="text-sm text-muted-foreground mb-1">Net Calories</p>
                <p className="text-2xl font-bold">{weeklyStats.netCalories.toFixed(0)} kcal</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </Layout>
  );
};

export default Statistics;
