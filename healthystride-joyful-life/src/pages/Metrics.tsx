import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Calendar as CalendarIcon, Save, Plus } from "lucide-react";
import { Layout } from "@/components/Layout";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Calendar } from "@/components/ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { api, HealthMetrics } from "@/lib/api";
import { useUser } from "@/contexts/UserContext";
import { toast } from "sonner";
import { format } from "date-fns";
import { cn } from "@/lib/utils";

const Metrics = () => {
  const { currentUserId } = useUser();
  const navigate = useNavigate();
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [formData, setFormData] = useState<HealthMetrics>({
    date: format(new Date(), "yyyy-MM-dd"),
    steps: 0,
    caloriesConsumed: 0,
    caloriesBurned: 0,
    distanceKm: 0,
    activeMinutes: 0,
    waterIntakeLiters: 0,
    sleepHours: 0,
    heartRateAvg: 0
  });

  useEffect(() => {
    if (!currentUserId) {
      navigate("/users");
      return;
    }
  }, [currentUserId, navigate]);

  useEffect(() => {
    if (currentUserId) {
      loadMetricsForDate(format(selectedDate, "yyyy-MM-dd"));
    }
  }, [selectedDate, currentUserId]);

  const loadMetricsForDate = async (date: string) => {
    if (!currentUserId) return;
    
    try {
      const metrics = await api.metrics.getByDate(currentUserId, date);
      setFormData(metrics || { date, steps: 0, caloriesConsumed: 0, caloriesBurned: 0, distanceKm: 0, activeMinutes: 0, waterIntakeLiters: 0, sleepHours: 0, heartRateAvg: 0 });
    } catch (error) {
      setFormData({ date, steps: 0, caloriesConsumed: 0, caloriesBurned: 0, distanceKm: 0, activeMinutes: 0, waterIntakeLiters: 0, sleepHours: 0, heartRateAvg: 0 });
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUserId) return;

    try {
      await api.metrics.create(currentUserId, formData);
      // Success toast is handled by API service
    } catch (error) {
      // Error toast is handled by API service
    }
  };

  const handleQuickAddSteps = async () => {
    if (!currentUserId) return;
    
    try {
      await api.metrics.addSteps(currentUserId, 1000, formData.date);
      // Success toast is handled by API service
      loadMetricsForDate(formData.date);
    } catch (error) {
      // Error toast is handled by API service
    }
  };

  const netCalories = (formData.caloriesConsumed || 0) - (formData.caloriesBurned || 0);

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Daily Metrics</h1>
          <Popover>
            <PopoverTrigger asChild>
              <Button variant="outline" className="gap-2">
                <CalendarIcon className="h-4 w-4" />
                {format(selectedDate, "MMM d, yyyy")}
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-auto p-0" align="end">
              <Calendar
                mode="single"
                selected={selectedDate}
                onSelect={(date) => date && setSelectedDate(date)}
                initialFocus
                className={cn("p-3 pointer-events-auto")}
              />
            </PopoverContent>
          </Popover>
        </div>

        <form onSubmit={handleSave}>
          <Card>
            <CardHeader>
              <CardTitle>Health Metrics for {format(selectedDate, "MMMM d, yyyy")}</CardTitle>
              <CardDescription>Track your daily health and fitness data</CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
              <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <Label htmlFor="steps">Steps</Label>
                    <Button type="button" variant="ghost" size="sm" onClick={handleQuickAddSteps}>
                      <Plus className="h-3 w-3 mr-1" />
                      1000
                    </Button>
                  </div>
                  <Input
                    id="steps"
                    type="number"
                    min="0"
                    value={formData.steps || ""}
                    onChange={(e) => setFormData({ ...formData, steps: parseInt(e.target.value) || 0 })}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="calories-consumed">Calories Consumed (kcal)</Label>
                  <Input
                    id="calories-consumed"
                    type="number"
                    min="0"
                    step="0.1"
                    value={formData.caloriesConsumed || ""}
                    onChange={(e) => setFormData({ ...formData, caloriesConsumed: parseFloat(e.target.value) || 0 })}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="calories-burned">Calories Burned (kcal)</Label>
                  <Input
                    id="calories-burned"
                    type="number"
                    min="0"
                    step="0.1"
                    value={formData.caloriesBurned || ""}
                    onChange={(e) => setFormData({ ...formData, caloriesBurned: parseFloat(e.target.value) || 0 })}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="distance">Distance (km)</Label>
                  <Input
                    id="distance"
                    type="number"
                    min="0"
                    step="0.01"
                    value={formData.distanceKm || ""}
                    onChange={(e) => setFormData({ ...formData, distanceKm: parseFloat(e.target.value) || 0 })}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="active-minutes">Active Minutes</Label>
                  <Input
                    id="active-minutes"
                    type="number"
                    min="0"
                    value={formData.activeMinutes || ""}
                    onChange={(e) => setFormData({ ...formData, activeMinutes: parseInt(e.target.value) || 0 })}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="water">Water Intake (liters)</Label>
                  <Input
                    id="water"
                    type="number"
                    min="0"
                    step="0.1"
                    value={formData.waterIntakeLiters || ""}
                    onChange={(e) => setFormData({ ...formData, waterIntakeLiters: parseFloat(e.target.value) || 0 })}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="sleep">Sleep Hours</Label>
                  <Input
                    id="sleep"
                    type="number"
                    min="0"
                    step="0.1"
                    value={formData.sleepHours || ""}
                    onChange={(e) => setFormData({ ...formData, sleepHours: parseFloat(e.target.value) || 0 })}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="heart-rate">Average Heart Rate (bpm)</Label>
                  <Input
                    id="heart-rate"
                    type="number"
                    min="0"
                    value={formData.heartRateAvg || ""}
                    onChange={(e) => setFormData({ ...formData, heartRateAvg: parseInt(e.target.value) || 0 })}
                  />
                </div>
              </div>

              <div className="pt-4 border-t">
                <div className="flex items-center justify-between p-4 rounded-lg bg-muted">
                  <span className="font-medium">Net Calories</span>
                  <span className="text-2xl font-bold">{netCalories.toFixed(0)} kcal</span>
                </div>
              </div>

              <Button type="submit" className="w-full gap-2">
                <Save className="h-4 w-4" />
                Save Metrics
              </Button>
            </CardContent>
          </Card>
        </form>
      </div>
    </Layout>
  );
};

export default Metrics;
