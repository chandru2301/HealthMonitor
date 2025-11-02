import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { User as UserIcon, Edit, Activity, Heart, TrendingUp } from "lucide-react";
import { Layout } from "@/components/Layout";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { api, User } from "@/lib/api";
import { useUser } from "@/contexts/UserContext";
import { toast } from "sonner";
import { format, differenceInYears } from "date-fns";

const Profile = () => {
  const { currentUserId } = useUser();
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);
  const [bmr, setBmr] = useState<number>(0);
  const [tdee, setTdee] = useState<number>(0);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [formData, setFormData] = useState<User | null>(null);

  useEffect(() => {
    if (!currentUserId) {
      navigate("/users");
      return;
    }
    loadUserData();
  }, [currentUserId, navigate]);

  const loadUserData = async () => {
    if (!currentUserId) return;
    
    try {
      const [userData, bmrData, tdeeData] = await Promise.all([
        api.users.getById(currentUserId),
        api.users.getBMR(currentUserId),
        api.users.getTDEE(currentUserId)
      ]);
      
      setUser(userData);
      setFormData(userData);
      setBmr(bmrData);
      setTdee(tdeeData);
    } catch (error) {
      // Error toast is handled by API service
      console.error("Failed to load profile:", error);
    }
  };

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentUserId || !formData) return;

    try {
      await api.users.update(currentUserId, formData);
      // Success toast is handled by API service
      setDialogOpen(false);
      loadUserData();
    } catch (error) {
      // Error toast is handled by API service
    }
  };

  if (!user) return null;

  const age = differenceInYears(new Date(), new Date(user.dateOfBirth));
  const bmi = user.weightKg / ((user.heightCm / 100) ** 2);
  const heightFeet = Math.floor(user.heightCm / 30.48);
  const heightInches = Math.round((user.heightCm / 30.48 - heightFeet) * 12);
  const weightLbs = (user.weightKg * 2.20462).toFixed(1);

  const getBmiStatus = (bmi: number) => {
    if (bmi < 18.5) return { text: "Underweight", color: "text-blue-500" };
    if (bmi < 25) return { text: "Normal", color: "text-success" };
    if (bmi < 30) return { text: "Overweight", color: "text-accent" };
    return { text: "Obese", color: "text-destructive" };
  };

  const bmiStatus = getBmiStatus(bmi);

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-3xl font-bold">Profile</h1>
          <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
            <DialogTrigger asChild>
              <Button>
                <Edit className="h-4 w-4 mr-2" />
                Edit Profile
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>Edit Profile</DialogTitle>
                <DialogDescription>Update your personal information</DialogDescription>
              </DialogHeader>
              {formData && (
                <form onSubmit={handleUpdate} className="space-y-4">
                  <div className="grid gap-4 md:grid-cols-2">
                    <div className="space-y-2">
                      <Label htmlFor="name">Full Name *</Label>
                      <Input
                        id="name"
                        required
                        value={formData.name}
                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="email">Email *</Label>
                      <Input
                        id="email"
                        type="email"
                        required
                        value={formData.email}
                        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="dob">Date of Birth *</Label>
                      <Input
                        id="dob"
                        type="date"
                        required
                        value={formData.dateOfBirth}
                        onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="gender">Gender *</Label>
                      <Select
                        value={formData.gender}
                        onValueChange={(value: any) => setFormData({ ...formData, gender: value })}
                      >
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="MALE">Male</SelectItem>
                          <SelectItem value="FEMALE">Female</SelectItem>
                          <SelectItem value="OTHER">Other</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="height">Height (cm) *</Label>
                      <Input
                        id="height"
                        type="number"
                        required
                        min="50"
                        max="300"
                        value={formData.heightCm}
                        onChange={(e) => setFormData({ ...formData, heightCm: parseFloat(e.target.value) })}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="weight">Weight (kg) *</Label>
                      <Input
                        id="weight"
                        type="number"
                        required
                        min="20"
                        max="500"
                        value={formData.weightKg}
                        onChange={(e) => setFormData({ ...formData, weightKg: parseFloat(e.target.value) })}
                      />
                    </div>
                    <div className="space-y-2 md:col-span-2">
                      <Label htmlFor="activity">Activity Level</Label>
                      <Select
                        value={formData.activityLevel}
                        onValueChange={(value: any) => setFormData({ ...formData, activityLevel: value })}
                      >
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="SEDENTARY">Sedentary (Little or no exercise)</SelectItem>
                          <SelectItem value="LIGHTLY_ACTIVE">Lightly Active (1-3 days/week)</SelectItem>
                          <SelectItem value="MODERATELY_ACTIVE">Moderately Active (3-5 days/week)</SelectItem>
                          <SelectItem value="VERY_ACTIVE">Very Active (6-7 days/week)</SelectItem>
                          <SelectItem value="EXTRA_ACTIVE">Extra Active (Physical job + exercise)</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>
                  <Button type="submit" className="w-full">Save Changes</Button>
                </form>
              )}
            </DialogContent>
          </Dialog>
        </div>

        <div className="grid gap-6 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <UserIcon className="h-5 w-5" />
                Personal Information
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-muted-foreground">Name</p>
                  <p className="font-semibold">{user.name}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Email</p>
                  <p className="font-semibold">{user.email}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Date of Birth</p>
                  <p className="font-semibold">{format(new Date(user.dateOfBirth), "MMM d, yyyy")}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Age</p>
                  <p className="font-semibold">{age} years</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Gender</p>
                  <p className="font-semibold">{user.gender}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Activity Level</p>
                  <p className="font-semibold">{user.activityLevel?.replace("_", " ")}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Activity className="h-5 w-5" />
                Body Measurements
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-muted-foreground">Height</p>
                  <p className="font-semibold">{user.heightCm} cm</p>
                  <p className="text-xs text-muted-foreground">{heightFeet}' {heightInches}"</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Weight</p>
                  <p className="font-semibold">{user.weightKg} kg</p>
                  <p className="text-xs text-muted-foreground">{weightLbs} lbs</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="md:col-span-2">
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Heart className="h-5 w-5" />
                Health Metrics
              </CardTitle>
              <CardDescription>Calculated based on your profile</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid gap-6 md:grid-cols-3">
                <div className="text-center p-6 rounded-lg bg-gradient-to-br from-primary/10 to-primary-glow/10 border border-primary/20">
                  <TrendingUp className="h-8 w-8 text-primary mx-auto mb-2" />
                  <p className="text-sm text-muted-foreground mb-1">BMI</p>
                  <p className="text-3xl font-bold">{bmi.toFixed(1)}</p>
                  <p className={`text-sm font-semibold ${bmiStatus.color}`}>{bmiStatus.text}</p>
                </div>
                <div className="text-center p-6 rounded-lg bg-gradient-to-br from-accent/10 to-accent/5 border border-accent/20">
                  <Activity className="h-8 w-8 text-accent mx-auto mb-2" />
                  <p className="text-sm text-muted-foreground mb-1">BMR</p>
                  <p className="text-3xl font-bold">{bmr.toFixed(0)}</p>
                  <p className="text-sm text-muted-foreground">kcal/day</p>
                </div>
                <div className="text-center p-6 rounded-lg bg-gradient-to-br from-success/10 to-success/5 border border-success/20">
                  <Heart className="h-8 w-8 text-success mx-auto mb-2" />
                  <p className="text-sm text-muted-foreground mb-1">TDEE</p>
                  <p className="text-3xl font-bold">{tdee.toFixed(0)}</p>
                  <p className="text-sm text-muted-foreground">kcal/day</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </Layout>
  );
};

export default Profile;
